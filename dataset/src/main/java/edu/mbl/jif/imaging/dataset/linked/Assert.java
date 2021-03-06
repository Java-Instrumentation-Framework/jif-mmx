/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mbl.jif.imaging.dataset.linked;

/**
 *
 * @author GBH
 */
 
 /**
  * Translatable version of JUnit's <code>Assert</code>.
  */
 public class Assert {
   public static void assertEquals(boolean obj1, boolean obj2) {
     assertEquals("", obj1, obj2);
   }
 
   public static void assertEquals(byte obj1, byte obj2) {
     assertEquals("", obj1, obj2);
   }
 
   public static void assertEquals(char obj1, char obj2) {
     assertEquals("", obj1, obj2);
   }
 
   public static void assertEquals(double obj1, double obj2, double delta) {
     assertEquals("", obj1, obj2, delta);
   }
 
   public static void assertEquals(float obj1, float obj2, float delta) {
     assertEquals("", obj1, obj2, delta);
   }
 
   public static void assertEquals(int expected, int actual) {
     assertEquals("", expected, actual);
   }
 
   public static void assertEquals(long obj1, long obj2) {
     assertEquals("", obj1, obj2);
   }
 
   public static void assertEquals(Object obj1, Object obj2) {
     assertEquals("", obj1, obj2);
   }
 
   public static void assertEquals(short expected, short actual) {
     assertEquals("", expected, actual);
   }
 
   public static void assertEquals(String str, boolean obj1, boolean obj2) {
     assertEquals(str, Boolean.valueOf(obj1), Boolean.valueOf(obj2));
   }
 
   public static void assertEquals(String str, byte obj1, byte obj2) {
     assertEquals(str, Byte.valueOf(obj1), Byte.valueOf(obj2));
   }
 
   public static void assertEquals(String str, char obj1, char obj2) {
     assertEquals(str, Character.valueOf(obj1), Character.valueOf(obj2));
   }
 
   public static void assertEquals(String str, double obj1, double obj2,
       double delta) {
     if (obj1 == obj2) {
       return;
     } else if (Math.abs(obj1 - obj2) <= delta) {
       return;
     } else {
       fail(str + " expected=" + obj1 + " actual=" + obj2 + " delta=" + delta);
     }
   }
 
   public static void assertEquals(String str, float obj1, float obj2,
       float delta) {
     if (obj1 == obj2) {
       return;
     } else if (Math.abs(obj1 - obj2) <= delta) {
       return;
     } else {
       fail(str + " expected=" + obj1 + " actual=" + obj2 + " delta=" + delta);
     }
   }
 
   public static void assertEquals(String msg, int expected, int actual) {
     if (expected != actual) {
       fail(msg + " expected=" + expected + " actual=" + actual);
     }
   }
 
   public static void assertEquals(String str, long obj1, long obj2) {
     assertEquals(str, new Long(obj1), new Long(obj2));
   }
 
   public static void assertEquals(String msg, Object obj1, Object obj2) {
     if (obj1 == null && obj2 == null) {
       return;
     }
 
     if (obj1 != null && obj1.equals(obj2)) {
       return;
     }
 
     fail(msg + " expected=" + obj1 + " actual=" + obj2);
   }
 
   public static void assertEquals(String str, short obj1, short obj2) {
     assertEquals(str, Short.valueOf(obj1), Short.valueOf(obj2));
   }
 
   public static void assertEquals(String obj1, String obj2) {
     assertEquals("", obj1, obj2);
   }
 
   public static void assertEquals(String message, String expected, String actual) {
     assertEquals(message, (Object) expected, (Object) actual);
   }
 
   public static void assertFalse(boolean condition) {
     assertFalse(null, condition);
   }
 
   public static void assertFalse(String message, boolean condition) {
     assertTrue(message, !condition);
   }
 
   public static void assertNotNull(Object obj) {
     assertNotNull(null, obj);
   }
 
   public static void assertNotNull(String msg, Object obj) {
     assertTrue(msg, obj != null);
   }
 
   public static void assertNotSame(Object obj1, Object obj2) {
     assertNotSame(null, obj1, obj2);
   }
 
   public static void assertNotSame(String msg, Object obj1, Object obj2) {
     if (obj1 != obj2) {
       return;
     }
 
     if (msg == null) {
       msg = "";
     }
 
     fail(msg + " expected and actual match");
   }
 
   public static void assertNull(Object obj) {
     assertNull(null, obj);
   }
 
   public static void assertNull(String msg, Object obj) {
     assertTrue(msg, obj == null);
   }
 
   public static void assertSame(Object obj1, Object obj2) {
     assertSame(null, obj1, obj2);
   }
 
   public static void assertSame(String msg, Object obj1, Object obj2) {
     if (obj1 == obj2) {
       return;
     }
 
     if (msg == null) {
       msg = "";
     }
 
     fail(msg + " expected and actual do not match");
   }
 
   public static void assertTrue(boolean condition) {
     assertTrue(null, condition);
   }
 
   public static void assertTrue(String message, boolean condition) {
     if (!condition) {
       fail(message);
     }
   }
 
   public static void fail() {
     fail(null);
   }
 
   public static void fail(String message) {
		 System.err.println("Error: " + message);
     //throw new AssertionFailedError(message);
   }
 
   public static void failNotEquals(String message, Object expected,
       Object actual) {
     String formatted = "";
     if (message != null) {
       formatted = message + " ";
     }
     fail(formatted + "expected :<" + expected + "> was not:<" + actual + ">");
   }
 
   public static void failNotSame(String message, Object expected, Object actual) {
     String formatted = "";
     if (message != null) {
       formatted = message + " ";
     }
     fail(formatted + "expected same:<" + expected + "> was not:<" + actual
         + ">");
   }
 
   public static void failSame(String message) {
     String formatted = "";
     if (message != null) {
       formatted = message + " ";
     }
     fail(formatted + "expected not same");
   }
 
   /**
    * Utility class, no public constructor needed.
    */
   protected Assert() {
   }
 }
	