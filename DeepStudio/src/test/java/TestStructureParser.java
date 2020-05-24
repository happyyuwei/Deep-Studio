import org.deepstudio.compile.StructureParser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;


public class TestStructureParser {


    @Test
    public void testRevConnections(){

        {
            //案例1
            //输入
            Map<String, List<String>> input = new HashMap<>();
            input.put("1", Arrays.asList("2"));
            input.put("2", Arrays.asList("3", "4"));
            input.put("3", new ArrayList<>());
            input.put("4", new ArrayList<>());

            //标准输出
            Map<String, List<String>> expect = new HashMap<>();
            expect.put("1", new ArrayList<>());
            expect.put("2", Arrays.asList("1"));
            expect.put("3", Arrays.asList("2"));
            expect.put("4", Arrays.asList("2"));
            assertEquals(expect, StructureParser.revConnections(input));
        }
        {
            //案例2
            //输入
            Map<String, List<String>> input = new HashMap<>();
            input.put("1", Arrays.asList("2", "5"));
            input.put("2", Arrays.asList("3", "4"));
            input.put("5", Arrays.asList("4"));
            input.put("4", new ArrayList<>());
            input.put("3", new ArrayList<>());

            //标准输出
            Map<String, List<String>> expect = new HashMap<>();
            expect.put("2", Arrays.asList("1"));
            expect.put("3", Arrays.asList("2"));
            expect.put("4", Arrays.asList("2", "5"));
            expect.put("5", Arrays.asList("1"));
            expect.put("1", new ArrayList<>());
            assertEquals(expect, StructureParser.revConnections(input));
        }
    }

    @Test
    public void testRemoveConnection(){
        {
            //案例1
            //输入
            Map<String, List<String>> input = new HashMap<>();
            input.put("1", Arrays.asList("2"));
            input.put("2", Arrays.asList("3", "4"));
            input.put("3", new ArrayList<>());
            input.put("4", new ArrayList<>());

            //标准输出
            Map<String, List<String>> expect = new HashMap<>();
            expect.put("1", new ArrayList<>());
            expect.put("3", new ArrayList<>());
            expect.put("4", new ArrayList<>());

            assertEquals(expect, StructureParser.removeConnection(input,Arrays.asList("2")));
        }
        {
            //案例2
            //输入
            Map<String, List<String>> input = new HashMap<>();
            input.put("1", Arrays.asList("2", "5"));
            input.put("2", Arrays.asList("3", "4"));
            input.put("5", Arrays.asList("4"));
            input.put("4", new ArrayList<>());
            input.put("3", new ArrayList<>());

            //标准输出
            Map<String, List<String>> expect = new HashMap<>();
            expect.put("1", Arrays.asList("5"));
            expect.put("5", Arrays.asList("4"));
            expect.put("4", new ArrayList<>());
            expect.put("3", new ArrayList<>());

            assertEquals(expect, StructureParser.removeConnection(input,Arrays.asList("2")));
        }
        {
            //案例3
            //输入
            Map<String, List<String>> input = new HashMap<>();
            input.put("1", Arrays.asList("2", "5"));
            input.put("2", Arrays.asList("3", "4"));
            input.put("5", Arrays.asList("4"));
            input.put("4", new ArrayList<>());
            input.put("3", new ArrayList<>());

            //标准输出
            Map<String, List<String>> expect = new HashMap<>();
            expect.put("2", Arrays.asList("3", "4"));
            expect.put("5", Arrays.asList("4"));
            expect.put("4", new ArrayList<>());
            expect.put("3", new ArrayList<>());

            assertEquals(expect, StructureParser.removeConnection(input,Arrays.asList("1")));
        }
    }

    @Test
    public void testSortConnections(){
        {
            //案例1
            //输入
            Map<String, List<String>> input = new HashMap<>();
            input.put("1", Arrays.asList("2", "5"));
            input.put("2", Arrays.asList("3", "4"));
            input.put("5", Arrays.asList("4"));
            input.put("4", new ArrayList<>());
            input.put("3", new ArrayList<>());

            //期望值
            List<List<String>> expect=new ArrayList<>();
            expect.add(Arrays.asList("1"));
            expect.add(Arrays.asList("2","5"));
            expect.add(Arrays.asList("3","4"));
            assertEquals(expect, StructureParser.sortConnections(input));
        }
    }
}
