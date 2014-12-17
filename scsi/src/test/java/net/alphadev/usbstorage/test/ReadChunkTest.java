package net.alphadev.usbstorage.test;

import net.alphadev.usbstorage.api.device.BulkDevice;
import net.alphadev.usbstorage.api.scsi.Transmittable;
import net.alphadev.usbstorage.bbb.BulkBlockDevice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.ByteBuffer;

/**
 * @author Jan Seeger <jan@alphadev.net>
 */
public class ReadChunkTest {
    @Mock
    private BulkDevice probe;

    private BulkBlockDevice instance;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        instance = new BulkBlockDevice(probe);
        instance.setBlockSize(2);
        instance.setMaxTransferSize(8);

        Mockito.when(probe.read(2)).thenReturn(new byte[]{9, 8})
                .thenReturn(new byte[]{7, 6})
                .thenReturn(new byte[]{5, 4})
                .thenReturn(new byte[]{3, 2})
                .thenReturn(new byte[]{1, 0})
                .thenReturn(new byte[]{9, 8});
        Mockito.when(probe.read(13)).thenReturn(deviceStatusOk());
    }

    @Test
    public void readHalfBlock() {
        final ByteBuffer bb = ByteBuffer.allocate(1);
        instance.read(0, bb);

        Mockito.verify(probe).write(Matchers.any(Transmittable.class));
        Mockito.verify(probe).read(2);
        Mockito.verify(probe).read(13);
        Mockito.verifyNoMoreInteractions(probe);

        byte[] expected = new byte[]{9};
        Assert.assertArrayEquals(expected, bb.array());
    }

    @Test
    public void readFullBlock() {
        final ByteBuffer bb = ByteBuffer.allocate(2);
        instance.read(0, bb);

        Mockito.verify(probe).write(Matchers.any(Transmittable.class));
        Mockito.verify(probe).read(2);
        Mockito.verify(probe).read(13);
        Mockito.verifyNoMoreInteractions(probe);


        byte[] expected = new byte[]{9, 8};
        Assert.assertArrayEquals(expected, bb.array());
    }

    @Test
    public void readHalfTransferUnit() {
        final ByteBuffer bb = ByteBuffer.allocate(3);
        instance.read(0, bb);

        Mockito.verify(probe).write(Matchers.any(Transmittable.class));
        Mockito.verify(probe, Mockito.times(2)).read(2);
        Mockito.verify(probe).read(13);
        Mockito.verifyNoMoreInteractions(probe);

        byte[] expected = new byte[]{9, 8, 7};
        Assert.assertArrayEquals(expected, bb.array());
    }

    @Test
    public void readTransferUnit() {
        final ByteBuffer bb = ByteBuffer.allocate(8);
        instance.read(0, bb);

        Mockito.verify(probe).write(Matchers.any(Transmittable.class));
        Mockito.verify(probe, Mockito.times(4)).read(2);
        Mockito.verify(probe).read(13);
        Mockito.verifyNoMoreInteractions(probe);

        byte[] expected = new byte[]{9, 8, 7, 6, 5, 4, 3, 2};
        Assert.assertArrayEquals(expected, bb.array());
    }

    @Test
    public void read12K() {
        final ByteBuffer bb = ByteBuffer.allocate(12);
        instance.read(0, bb);

        Mockito.verify(probe, Mockito.times(2)).write(Matchers.any(Transmittable.class));
        Mockito.verify(probe, Mockito.times(6)).read(2);
        Mockito.verify(probe, Mockito.times(2)).read(13);
        Mockito.verifyNoMoreInteractions(probe);

        byte[] expected = new byte[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 9, 8};
        Assert.assertArrayEquals(expected, bb.array());
    }

    @Test
    public void readCopesWithOverlength() {
        Mockito.when(probe.read(1)).thenReturn(new byte[]{9, 8});
        Mockito.when(probe.read(13)).thenReturn(deviceStatusOk());

        final ByteBuffer bb = ByteBuffer.allocate(1);
        instance.read(0, bb);

        Mockito.verify(probe).write(Matchers.any(Transmittable.class));
        Mockito.verify(probe).read(2);
        Mockito.verify(probe).read(13);
        Mockito.verifyNoMoreInteractions(probe);

        byte[] expected = new byte[]{9};
        Assert.assertArrayEquals(expected, bb.array());
    }

    private byte[] deviceStatusOk() {
        return new byte[]{
                'U', 'S', 'B', 'S', // signature
                0, 0, 0, 0, // tag
                0, 0, 0, 0, // residue
                0 // status
        };
    }
}
