package utils;

/**
 * 用于byte数组到ip地址、MAC地址、16进制数据的转换
 */
public class Transformate {


    public static String toHexString(int input,int length){
        String result=Integer.toHexString(input);
        int len=result.length();
        if(len<4){
            StringBuilder pre=new StringBuilder("");
            for(int i=0;i<length-len;i++){
                pre.append(0);
            }
            result=pre.append(result).toString();
        }else {
            result=result.substring(len-length,len);
        }

        return result;
    }
    public static String hexString(byte[] bytes,boolean append){
        int count=0;
        StringBuilder hex=new StringBuilder("");
        for(byte b:bytes) {
            count++;
            hex.append(toHexString(b,2));
           if(append){
               hex.append(" ");
               if(count%8==0){
                   hex.append("");
               }
               if (count%16==0){
                   hex.append("\n");
               }
           }

        }
        return hex.toString();
    }
}
