package com.tbkk.yoyo.rpc.test;



import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;
import com.tbkk.yoyo.rpc.net.serializer.AbstractSerializer;

import java.util.Random;
import java.util.UUID;

/**
 * @author tbkk 2019-10-30 21:02:55
 */
public class SerializerTest {

    public static void main(String[] args) {
//        //Serializer serializer = Serializer.SerializeEnum.match("PROTOSTUFF", null).getSerializer();
//        Serializer serializer = Serializer.SerializeEnum.PROTOSTUFF.getSerializer();
//        //Serializer serializer = Serializer.SerializeEnum.KRYO.getSerializer();
//
//        System.out.println(serializer);
//        try {
//            Map<String, String> map = new HashMap<>();
//            map.put("aaa", "111");
//            map.put("bbb", "222");
//            serializer.deserialize(serializer.serialize("ssssss"), String.class);
//            long startTime = System.nanoTime();
//            for (int i = 0; i < 1000; i++)
//            {
//                System.out.println(serializer.deserialize(serializer.serialize("ddddddd"), String.class));
//            }
//            System.out.println(" spend " + (System.nanoTime() - startTime) * 0.000001 / 1000);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        HESSIAN(HessianSerializer.class),
//                HESSIAN1(Hessian1Serializer.class),
//                PROTOSTUFF(ProtostuffSerializer.class),
//                KRYO(KryoSerializer.class),
//                JACKSON(JacksonSerializer.class);

        serializerTest("PROTOSTUFF");
        serializerTest("KRYO");
        serializerTest("HESSIAN");
        serializerTest("HESSIAN1");
        serializerTest("JACKSON");
        //protoSerializerTest();

    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static void serializerTest(String serializerType) {
        AbstractSerializer serializer = AbstractSerializer.SerializeEnum.match(serializerType, null).getSerializer();
        //System.out.println(serializer);
        try {
           // serializer.deserialize(serializer.serialize("ssssss"), String.class);
            RpcRequest yoyoRpcRequest = new RpcRequest();
            yoyoRpcRequest.setRequestId(UUID.randomUUID().toString());
            yoyoRpcRequest.setCreateMillisTime(System.currentTimeMillis());
            yoyoRpcRequest.setAccessToken("1111");
            yoyoRpcRequest.setClassName("22222222233333333333333333322222");
            yoyoRpcRequest.setMethodName("22222222222222233333333333");
            serializer.deserialize(serializer.serialize(yoyoRpcRequest), RpcRequest.class);

            long startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++)
            {
                serializer.deserialize(serializer.serialize(yoyoRpcRequest), RpcRequest.class);
            }
            System.out.println(serializer.getClass().getSimpleName() + " spend " + (System.nanoTime() - startTime) * 0.000001);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
