package com.myself.everythingP.core.common;

import com.myself.everythingP.core.model.Thing;

public class Message {
    public static String print(Thing thing) {
        StringBuilder sb = new StringBuilder();
        sb.append(thing.getName()).append("  ")
                .append(thing.getPath()).append("  ");
        long len = thing.getLength();
        StringBuilder s = new StringBuilder();
        if (len >= 1000) {
            String l = String.valueOf(len);
            int count = 0;
            for (int i = l.length() - 1; i >= 0; i--) {
                s.append(l.charAt(i));
                count++;
                if (count == 3 && i != 0) {
                    s.append(",");
                    count = 0;
                }
            }
            s.reverse();
            sb.append(s.toString());
        } else {
            sb.append(len);
        }
        sb.append(" KB").append("  ").append(thing.getLastModifyTime());
        return sb.toString();
    }
}
