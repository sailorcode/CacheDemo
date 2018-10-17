
package com.trendytech.tcmp.queryengine.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangc on 2017/1/7.
 */
public class IPValidateUtil
{

    private static final String IPV4_REGEX = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

    private static final String IPV6_REGEX = "^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:)|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}(:[0-9A-Fa-f]{1,4}){1,2})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){1,3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){1,4})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){1,5})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){1,6})|(:(:[0-9A-Fa-f]{1,4}){1,7})|(([0-9A-Fa-f]{1,4}:){6}(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){5}:(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}:(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}:(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3})|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}:(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3})|([0-9A-Fa-f]{1,4}:(:[0-9A-Fa-f]{1,4}){0,4}:(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3})|(:(:[0-9A-Fa-f]{1,4}){0,5}:(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])(\\\\.(\\\\d|[1-9]\\\\d|1\\\\d{2}|2[0-4]\\\\d|25[0-5])){3}))$";

    /**
     * 是否为IPV4格式IP
     * @param ip
     * @return
     */
    public static boolean isIPv4(String ip)
    {
        if(ip.matches(IPV4_REGEX))
        {
            return true;
        }
        return false;
    }

    /**
     * 是否为IPV6格式IP
     * @param ip
     * @return
     */
    public static boolean isIPv6(String ip)
    {
        if(ip.matches(IPV6_REGEX))
        {
            return true;
        }
        return false;
    }

    /**
     * 将IPv6格式ip转换为数字
     * @param ipv6
     * @return
     */
    public static BigInteger ipv6toNum(String ipv6)
    {

        int compressIndex = ipv6.indexOf("::");
        if(compressIndex != -1)
        {
            String part1s = ipv6.substring(0, compressIndex);
            String part2s = ipv6.substring(compressIndex + 1);
            BigInteger part1 = ipv6toNum(part1s);
            BigInteger part2 = ipv6toNum(part2s);
            int part1hasDot = 0;
            char ch[] = part1s.toCharArray();
            for(char c : ch)
            {
                if(c == ':')
                {
                    part1hasDot++;
                }
            }
            // ipv6 has most 7 dot
            return part1.shiftLeft(16 * (7 - part1hasDot)).add(part2);
        }
        String[] str = ipv6.split(":");
        BigInteger big = BigInteger.ZERO;
        for(int i = 0; i < str.length; i++)
        {
            if(str[i].isEmpty())
            {
                str[i] = "0";
            }
            big = big.add(
                BigInteger.valueOf(Long.valueOf(str[i], 16)).shiftLeft(16 * (str.length - i - 1)));
        }
        return big;
    }

    /**
     * 将IPv4格式ip转换为数字
     * @param ipv4
     * @return
     */
    public static long ipv4toNum(String ipv4)
    {
        String[] splits = ipv4.split("\\.");
        long l = 0;
        l = l + (Long.valueOf(splits[0], 10)) << 24;
        l = l + (Long.valueOf(splits[1], 10) << 16);
        l = l + (Long.valueOf(splits[2], 10) << 8);
        l = l + (Long.valueOf(splits[3], 10));
        return l;
    }

    /**
     * 校验IPV4格式IP是否在IP段内
     * @param ip
     * @param ipRange
     * @param split
     * @return
     */
    public static boolean ipV4InRange(String ip, List<String> ipRange, String split)
    {
        for(String range : ipRange)
        {
            String startIp = range.split(split)[0];
            String endIp = range.split(split)[1];
            if(ipv4toNum(ip) >= ipv4toNum(startIp) && ipv4toNum(ip) <= ipv4toNum(endIp))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 校验IPV6格式IP是否在IP段内
     * @param ip
     * @param ipRange
     * @param split
     * @return
     */
    public static boolean ipV6InRange(String ip, List<String> ipRange, String split)
    {
        for(String range : ipRange)
        {
            String startIp = range.split(split)[0];
            String endIp = range.split(split)[1];
            if((ipv6toNum(ip).compareTo(ipv6toNum(startIp)) == 1
                || ipv6toNum(ip).compareTo(ipv6toNum(startIp)) == 0)
                && (ipv6toNum(ip).compareTo(ipv6toNum(endIp)) == -1
                    || ipv6toNum(ip).compareTo(ipv6toNum(endIp)) == 0))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据起始ip和结束ip获取范围内所有ip
     * 示例 ipfrom="172.100.1.1"
     * @param ipfrom
     * @param ipto
     * @return
     */
    public static List<String> getAllIps(String ipfrom,String ipto){
        List<String> ips = new ArrayList<String>();
        String[] ipfromd = ipfrom.split("\\.");
        String[] iptod = ipto.split("\\.");
        int[] int_ipf = new int[4];
        int[] int_ipt = new int[4];
        for (int i = 0; i < 4; i++) {
            int_ipf[i] = Integer.parseInt(ipfromd[i]);
            int_ipt[i] = Integer.parseInt(iptod[i]);
        }
        for (int A = int_ipf[0]; A <= int_ipt[0]; A++) {
            for (int B = (A == int_ipf[0] ? int_ipf[1] : 0); B <= (A == int_ipt[0] ? int_ipt[1]
                    : 255); B++) {
                for (int C = (B == int_ipf[1] ? int_ipf[2] : 0); C <= (B == int_ipt[1] ? int_ipt[2]
                        : 255); C++) {
                    for (int D = (C == int_ipf[2] ? int_ipf[3] : 0); D <= (C == int_ipt[2] ? int_ipt[3]
                            : 255); D++) {
                        ips.add(new String(A + "." + B + "." + C + "." + D));
                    }
                }
            }
        }
        return ips;
    }

    /**
     * 根据掩码位计算掩码
     */
    public static String getMask(int masks)
    {
        if (masks == 1) {
            return "128.0.0.0";
        } else if (masks == 2) {
            return "192.0.0.0";
        } else if (masks == 3) {
            return "224.0.0.0";
        } else if (masks == 4) {
            return "240.0.0.0";
        } else if (masks == 5) {
            return "248.0.0.0";
        } else if (masks == 6) {
            return "252.0.0.0";
        } else if (masks == 7) {
            return "254.0.0.0";
        } else if (masks == 8) {
            return "255.0.0.0";
        } else if (masks == 9) {
            return "255.128.0.0";
        } else if (masks == 10) {
            return "255.192.0.0";
        } else if (masks == 11) {
            return "255.224.0.0";
        } else if (masks == 12) {
            return "255.240.0.0";
        } else if (masks == 13) {
            return "255.248.0.0";
        } else if (masks == 14) {
            return "255.252.0.0";
        } else if (masks == 15) {
            return "255.254.0.0";
        } else if (masks == 16) {
            return "255.255.0.0";
        } else if (masks == 17) {
            return "255.255.128.0";
        } else if (masks == 18) {
            return "255.255.192.0";
        } else if (masks == 19) {
            return "255.255.224.0";
        } else if (masks == 20) {
            return "255.255.240.0";
        } else if (masks == 21) {
            return "255.255.248.0";
        } else if (masks == 22) {
            return "255.255.252.0";
        } else if (masks == 23) {
            return "255.255.254.0";
        } else if (masks == 24) {
            return "255.255.255.0";
        } else if (masks == 25) {
            return "255.255.255.128";
        } else if (masks == 26) {
            return "255.255.255.192";
        } else if (masks == 27) {
            return "255.255.255.224";
        } else if (masks == 28) {
            return "255.255.255.240";
        } else if (masks == 29) {
            return "255.255.255.248";
        } else if (masks == 30) {
            return "255.255.255.252";
        } else if (masks == 31) {
            return "255.255.255.254";
        } else if (masks == 32) {
            return "255.255.255.255";
        }
        return "";
    }
}
