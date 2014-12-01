package net.alphadev.usbstorage.api.tests;

import net.alphadev.usbstorage.api.Path;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class PathTest {
    @Test
    public void basicHasNoParentsTest() {
        Path instance = new Path("driveId/");
        Assert.assertNull(instance.getParent());
    }

    @Test
    public void basicInstantiationTest() {
        Path instance = new Path("driveId/test");
        Assert.assertEquals("test", instance.getName());
        Assert.assertEquals("driveId", instance.getDeviceId());
    }

    @Test
    public void basicHasNoDevIdTest() {
        Path instance = new Path("/test");
        Assert.assertNull(instance.getDeviceId());
    }

    @Test
    public void subItemInstantiationTest() {
        Path instance = new Path("driveId/test/abc");
        Assert.assertEquals("abc", instance.getName());
    }

    @Test
    public void subItemParentTest() {
        Path instance = new Path("driveId/test/abc");
        Assert.assertEquals("abc", instance.getName());
        Assert.assertEquals("test", instance.getParent().getName());
        Assert.assertNull(instance.getParent().getParent());
    }

    @Test
    public void iteratorInOrderTest() {
        Path instance = new Path("driveId/a/b/c/d");
        Iterator<String> iterator = instance.getIterator().iterator();
        Assert.assertEquals("a", iterator.next());
        Assert.assertEquals("b", iterator.next());
        Assert.assertEquals("c", iterator.next());
        Assert.assertEquals("d", iterator.next());
    }

    @Test
    public void appendToPathTest() {
        Path first = new Path("driveId/first");
        Assert.assertEquals("first", first.getName());
        Path second = Path.createWithAppended(first, "second");

        // verify the first Path is not altered
        Assert.assertEquals("first", first.getName());

        // but the second one is created correctly
        Assert.assertEquals("first", second.getParent().getName());
        Assert.assertEquals("second", second.getName());
    }

    @Test
    public void appendMultipleToPathTest() {
        Path first = new Path("driveId/first");
        Assert.assertEquals("first", first.getName());
        Path fourth = Path.createWithAppended(first, "second/third/fourth");
        Path third = fourth.getParent();
        Path second = third.getParent();

        Assert.assertEquals("fourth", fourth.getName());
        Assert.assertEquals("third", third.getName());
        Assert.assertEquals("second", second.getName());
    }

    @Test
    public void appendMultipleToPathOtherTest() {
        Path first = new Path("driveId/first");
        Assert.assertEquals("first", first.getName());
        Path fourth = Path.createWithAppended(first, "second", "third", "fourth");
        Path third = fourth.getParent();
        Path second = third.getParent();

        Assert.assertEquals("fourth", fourth.getName());
        Assert.assertEquals("third", third.getName());
        Assert.assertEquals("second", second.getName());
    }
}
