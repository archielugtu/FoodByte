/**
 * Name: DataQuery.java
 * Authors: Connor Macdonald
 * Date: 12/09/2019
 */

/**
 * A Note for @SuppressWarnings("unchecked"):
 * There is a few instances in this file where the java compiler thinks I am doing unchecked casts.
 * Because DataQuery is following a Factory Design Pattern it is wrong because the type checking has already occurred while
 * adding the constraint. So I have decided to suppress these warnings.
 *
 */

package seng202.teamsix.data;
import java.lang.reflect.*;
import java.util.*;


/**
 * PRIVATE SUPPORTING CLASSES
 */

/**
 * DataQueryMember is an abstract class to encapsulate functionality
 * between reading a class field and the return of a class method
 */
abstract class DataQueryMember {
    abstract Object get(Object obj);
    abstract Class<?> getType();
}

/**
 * DataQuery Field extends DataQueryMember allowing for generic access to a
 * class field data type or value.
 */
class DataQueryField extends DataQueryMember{
    private Field field;

    DataQueryField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    Object get(Object obj) {
        try {
            if (field != null){
                return field.get(obj);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    Class<?> getType() {
        return field.getType();
    }
}

/**
 * DataQueryMethod extends DataQueryMember allowing for generic access to a
 * class method return type and return value.
 */
class DataQueryMethod extends DataQueryMember{
    private Method method;

    DataQueryMethod(Method method) {
        this.method = method;
    }

    Object get(Object obj) {
        try {
            if (method != null) {
                return method.invoke(obj);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    Class<?> getType() {
        return method.getReturnType();
    }
}

/**
 * DataQueryConstraint is the base type of any constraint used for filtering
 */
abstract class DataQueryConstraint {
    DataQueryMember field;

    /**
     * Provides a method for checking if an object meets a constraint
     * @param obj the obj to check if it meets the constraint. Must be an object that contains the field
     * @return if obj passes constraint
     */
    abstract boolean accepts(Object obj);
}

/**
 * Query Constraint if a class member is within a range.
 * @param <T> must be data type of field member and implement Comparable
 */
@SuppressWarnings("unchecked")
class DataQueryConstraintRange<T extends Comparable<T>> extends DataQueryConstraint {
    T upper;
    T lower;

    boolean accepts(Object obj) {
        T value = (T) field.get(obj);
        return value.compareTo(upper) >= 0 && value.compareTo(lower) <= 0;
    }
}

/**
 * Query constraint if a class member is equal to comparision
 * @param <T> must be data type of field member and implement Comparable
 */
@SuppressWarnings("unchecked")
class DataQueryConstraintEqual<T extends Comparable<T>> extends DataQueryConstraint {
    T comparison;

    boolean accepts(Object obj) {
        T value = (T) field.get(obj);
        if (value != null) {
            return value.compareTo(comparison) == 0;
        }
        return false;
    }
}

/**
 * Query constraint that class member must match regex
 */
class DataQueryConstraintRegex extends DataQueryConstraint {
    String regex;

    boolean accepts(Object obj) {
        String value = (String) field.get(obj);
        return value.matches(regex);
    }
}

/**
 * DataQueryComparator is used during the sorting phase of DataQuery to determine the order of elements.
 */
class DataQueryComparator implements Comparator<UUID_Entity> {
    DataQueryMember member;
    int ordering;

    /**
     * @param member must be of type Comparable
     * @param reversed sets if to sort in reversed ordering
     */
    DataQueryComparator(DataQueryMember member, Boolean reversed) throws IllegalArgumentException{
        this.member = member;
        ordering = reversed ? 1 : -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(UUID_Entity ref_a, UUID_Entity ref_b) {
        // Get objects from reference
        UUID_Entity obj_a = StorageAccess.instance().get(ref_a);
        UUID_Entity obj_b = StorageAccess.instance().get(ref_b);

        try {
            // Get value of members in both objects
            Comparable field_a = (Comparable) member.get(obj_a);
            Comparable field_b = (Comparable) member.get(obj_b);

            // Compare values of fields
            return field_a.compareTo(field_b) * ordering;
        } catch(ClassCastException e) {
            return 0;
        }

    }
}


/**
 * DataQuery is an logical service that allows generic queries into StorageAccess objects with a list of constraints and
 * sorting by object variables and methods. This service should be used anytime in the program you want to selectively
 * get some item.
 * @param <T> the class you are doing the search on must be one of the StorageAccess classes
 */
public class DataQuery<T extends UUID_Entity> {

    // Members
    private final Class<T> dataClass;

    private int constraint_count = 0;
    private HashMap<Integer, DataQueryConstraint> constraint_list = new HashMap<>();

    private DataQueryMember sort_by_field = null;
    private boolean sort_reversed = false;



    /**
     * Constructor for DataQuery into StorageAccess. Java doesn't allow you to check type during runtime requiring this
     * work around.
     * @param dataClass this should be equal to T.class
     */
    public DataQuery(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    /**
     * Adds a constraint condition on a QueryField that it must be within a range
     * @param field_name name of QueryField on class
     * @param lower lower bound of range, must be instance of Comparable
     * @param upper upper bound of range, must be instance of Comparable
     * @param <I> type of field, must be instance of Comparable
     * @return constraint id handle to remove constraint if dynamically creating query
     * @throws IllegalArgumentException if field_name does not match any QueryField members on class or does not match type given
     */
    public <I extends Comparable<I>> int addConstraintRange(String field_name, I lower, I upper) throws IllegalArgumentException {
        DataQueryMember field = getFieldWithAnnotation(field_name);

        // Test field
        if(field == null) {
            throw new IllegalArgumentException(String.format("Field '%s' does not exist for class '%s'", field_name, dataClass.getName()));
        }

        if(field.getType() != lower.getClass() || field.getType() != upper.getClass()) {
            throw new IllegalArgumentException(String.format("Field '%s' does not have same type as arguments", field_name, dataClass.getName()));
        }

        // Create Constraint
        DataQueryConstraintRange<I> constraint = new DataQueryConstraintRange<>();
        constraint.field = field;
        constraint.lower = lower;
        constraint.upper = upper;

        // Add constraint to list
        int index = constraint_count++;
        constraint_list.put(index, constraint);
        return index;
    }

    /**
     * Adds a constraint condition on a QueryField that it must be equal to value
     * @param field_name name of QueryField on class
     * @param value comparison value QueryField must be equal to
     * @param <I> QueryField type must be instance of Comparable
     * @return constraint id handle to remove constraint if dynamically creating query
     * @throws IllegalArgumentException if field_name does not match any QueryField members on class or does not match type given
     */
    public <I extends Comparable<I>> int addConstraintEqual(String field_name, I value) throws IllegalArgumentException {
        DataQueryMember field = getFieldWithAnnotation(field_name);

        // Test field
        if(field == null) {
            throw new IllegalArgumentException(String.format("Field '%s' does not exist for class '%s'", field_name, dataClass.getName()));
        }

        if(field.getType() != value.getClass()) {
            throw new IllegalArgumentException(String.format("Field '%s' does not have same type as arguments", field_name, dataClass.getName()));
        }

        // Create Constraint
        DataQueryConstraintEqual<I> constraint = new DataQueryConstraintEqual<>();
        constraint.field = field;
        constraint.comparison = value;

        int index = constraint_count++;
        constraint_list.put(index, constraint);

        return index;
    }

    /**
     * Adds a constraint condition on QueryField that is must match the specified regex
     * @param field_name QueryField name
     * @param regex
     * @return
     * @throws IllegalArgumentException
     */
    public int addConstraintRegex(String field_name, String regex) throws IllegalArgumentException {
        DataQueryMember field = getFieldWithAnnotation(field_name);

        // Test field
        if(field == null) {
            throw new IllegalArgumentException(String.format("Field '%s' does not exist for class '%s'", field_name, dataClass.getName()));
        }

        if(field.getType() != String.class) {
            throw new IllegalArgumentException(String.format("Field '%s' does not have type string for class '%s'", field_name, dataClass.getName()));
        }

        // Create Constraint
        DataQueryConstraintRegex constraint = new DataQueryConstraintRegex();
        constraint.field = field;
        constraint.regex = regex;

        int index = constraint_count++;
        constraint_list.put(index, constraint);

        return index;
    }

    /**
     * Removes a constraint condition from filter
     * @param constraint_id id handle to constraint
     */
    public void removeConstraint(int constraint_id) {
        constraint_list.remove(constraint_id);
    }

    /**
     * Set QueryField to sort the filtered list by
     * @param field_name name of QueryField on class, must be of instance Comparable
     * @param reversed sets sorting to be in reverse order
     */
    public void sort_by(String field_name, boolean reversed) {
        DataQueryMember field = getFieldWithAnnotation(field_name);

        // Test field
        if(field == null) {
            throw new IllegalArgumentException(String.format("Field '%s' does not exist for class '%s'", field_name, dataClass.getName()));
        }

        sort_by_field = field;
        sort_reversed = reversed;
    }

    /**
     * Runs the query using the state which has been setup of constraints and sorting.
     * @return Filtered list of UUID_Entity's in sorted order
     * @throws IllegalStateException This is thrown under if state is incorrect, which shouldn't hopefully happen
     */
    @SuppressWarnings("unchecked")
    public List<UUID_Entity> runQuery() throws IllegalStateException{
        // Get all UUID Entities of type dataClass
        Set<? extends UUID_Entity> total_list = StorageAccess.instance().getAllByClassType(dataClass);
        if(total_list == null) {
            throw new IllegalStateException("DataQuery can only be run on objects extending UUID_Entity");
        }

        // Filter entities by running constraints
        ArrayList<UUID_Entity> filtered_list = new ArrayList<>();
        filtered_list.ensureCapacity(total_list.size());
        for(UUID_Entity ref : total_list) {
            // Check obj meets all constraints
            T obj = (T) StorageAccess.instance().get(ref);
            boolean accepted = true;
            for(DataQueryConstraint constraint : constraint_list.values()) {
                accepted &= constraint.accepts(obj);
            }

            // Append object reference to filtered list
            if(accepted) {
                filtered_list.add(ref);
            }
        }

        // Sort Filtered list
        if(sort_by_field != null) {
            try{
                filtered_list.sort(new DataQueryComparator(sort_by_field, sort_reversed));
            } catch(IllegalArgumentException e) {
                System.err.println("Cannot sort query since sorting field is not an instance of Comparable");
            }
        }

        return filtered_list;
    }

    /**
     * Returns a DataQueryMember if class has field_name.
     * @param field_name name of QueryField on class
     * @return DataQueryMember of QueryField or null if it does not exist
     */
    private DataQueryMember getFieldWithAnnotation(String field_name) {
        // Check fields
        for(Field field : this.dataClass.getDeclaredFields()) {
            QueryField annotation = field.getAnnotation(QueryField.class);
            if(annotation != null && (field.getName().equals(field_name) || annotation.value().equals(field_name))) {
                return new DataQueryField(field);
            }
        }

        // Check Members
        for(Method method : this.dataClass.getDeclaredMethods()) {
            QueryField annotation = method.getAnnotation(QueryField.class);
            if(annotation != null && (method.getName().equals(field_name) || annotation.value().equals(field_name))) {
                return new DataQueryMethod(method);
            }
        }

        return null;
    }
}
