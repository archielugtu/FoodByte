package seng202.teamsix.data;

import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class MenuTest {
    @Test
    public void testSetAndGetName(){
        Menu menus = new Menu();
        menus.setName("New Menu");
        assertEquals("New Menu", menus.getName());
    }

    @Test
    public void testSetAndGetDescription(){
        Menu menus = new Menu();
        menus.setDescription("This Menu Is New");
        assertEquals("This Menu Is New", menus.getDescription());
    }

}

