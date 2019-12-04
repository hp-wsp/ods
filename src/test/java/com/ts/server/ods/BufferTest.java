package com.ts.server.ods;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class BufferTest {

    @Test
    public void testBuffer(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("abc".getBytes());
        System.out.println("before compact:" + buffer);
        System.out.println(new String(buffer.array()));
        buffer.flip();
        System.out.println("after flip:" + buffer);
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println("after three gets:" + buffer);
        System.out.println("\t" + new String(buffer.array()));
        buffer.compact();
        System.out.println("after compact:" + buffer);
        System.out.println("\t" + new String(buffer.array()));
    }
}
