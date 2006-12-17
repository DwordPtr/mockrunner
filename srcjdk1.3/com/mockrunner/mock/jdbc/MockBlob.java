package com.mockrunner.mock.jdbc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockrunner.util.common.ArrayUtil;
import com.mockrunner.util.common.CollectionUtil;

/**
 * Mock implementation of <code>Blob</code>.
 */
public class MockBlob implements Blob, Cloneable
{
    private final static Log log = LogFactory.getLog(MockBlob.class);
    private List blobData;
    private boolean wasFreeCalled = false;
    
    public MockBlob(byte[] data)
    {
        blobData = ArrayUtil.getListFromByteArray(data);
    }
    
    public long length() throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        return blobData.size();
    }

    public byte[] getBytes(long pos, int length) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        length = verifyAndFixLength(pos, length);
        return ArrayUtil.getByteArrayFromList(blobData, (int)(pos - 1), length);
    }

    public InputStream getBinaryStream() throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        return new ByteArrayInputStream(ArrayUtil.getByteArrayFromList(blobData));
    }

    public InputStream getBinaryStream(long pos, long length) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        length = verifyAndFixLength(pos, (int)length);
        return new ByteArrayInputStream(ArrayUtil.getByteArrayFromList(blobData, (int)(pos - 1), (int)length));
    }

    public long position(byte[] pattern, long start) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        byte[] data = ArrayUtil.getByteArrayFromList(blobData);
        int index = ArrayUtil.indexOf(data, pattern, (int)(start - 1));
        if(-1 != index) index += 1;
        return index;
    }

    public long position(Blob pattern, long start) throws SQLException
    {
        return position(pattern.getBytes(1, (int)pattern.length()), start);
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        ArrayUtil.addBytesToList(bytes, blobData, (int)(pos - 1));
        return bytes.length;
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        ArrayUtil.addBytesToList(bytes, offset, len, blobData, (int)(pos - 1));
        return len;
    }

    public OutputStream setBinaryStream(long pos) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        return new BlobOutputStream((int)(pos - 1));
    }

    public void truncate(long len) throws SQLException
    {
        if(wasFreeCalled)
        {
            throw new SQLException("free() was called");
        }
        blobData = CollectionUtil.truncateList(blobData, (int)len);
    }
    
    public void free() throws SQLException
    {
        wasFreeCalled = true;
    }

    public boolean wasFreeCalled()
    {
        return wasFreeCalled;
    }

    public boolean equals(Object obj)
    {
        if(null == obj) return false;
        if(!obj.getClass().equals(this.getClass())) return false;
        MockBlob other = (MockBlob)obj;
        return blobData.equals(other.blobData);
    }

    public int hashCode()
    {
        return blobData.hashCode();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("Blob data: ");
        for(int ii = 0; ii < blobData.size(); ii++)
        {
            buffer.append("[" + blobData.get(ii).toString() + "] ");
        }
        return buffer.toString();
    }
    
    public Object clone()
    {
        try
        {
            MockBlob blob = (MockBlob)super.clone();
            blob.blobData = new ArrayList(blobData);
            return blob;
        }
        catch(CloneNotSupportedException exc)
        {
            log.error(exc.getMessage(), exc);
        }
        return null;
    }
    
    private int verifyAndFixLength(long pos, int length)
    {
        if(length < 0)
        {
            throw new IllegalArgumentException("length must be greater or equals 0");
        }
        if((length + (pos - 1)) > blobData.size())
        {
            return blobData.size() - (int)(pos - 1);
        }
        return length;
    }
    
    private class BlobOutputStream extends OutputStream
    {  
        private int index;
        
        public BlobOutputStream(int index)
        {
            this.index = index;
        }
        
        public void write(int byteValue) throws IOException
        {
            byte[] bytes = new byte[] {(byte)byteValue};
            ArrayUtil.addBytesToList(bytes, blobData, index);
            index++;
        }
    }
}
