package io.jpower.kcp.netty.internal;

import com.mmo4j.kcp.netty.internal.ReusableHashMap;
import com.mmo4j.kcp.netty.internal.ReusableIterator;

import org.junit.Test;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public class ReusableHashMapTest {

    @Test
    public void entrySetIterator() throws Exception {
        ReusableHashMap<String, Integer> m = new ReusableHashMap<>();
        m.put("abc", 1);
        m.put("123", 2);
        m.put("hehe", 3);

        ReusableIterator<Map.Entry<String, Integer>> itr = m.entrySet().iterator();
        Map.Entry<String, Integer> entry0 = itr.next();
        Map.Entry<String, Integer> entry1 = itr.next();
        Map.Entry<String, Integer> entry2 = itr.next();

        ReusableIterator<Map.Entry<String, Integer>> reItr = itr.rewind();
        assertTrue(reItr == itr);

        assertEquals(entry0.getKey(), itr.next().getKey());
        assertEquals(entry1.getKey(), itr.next().getKey());
        assertEquals(entry2.getKey(), itr.next().getKey());

        // exception
        try {
            itr.next();
            fail("should throw exception");
        } catch (NoSuchElementException ignored) {

        }
    }

}
