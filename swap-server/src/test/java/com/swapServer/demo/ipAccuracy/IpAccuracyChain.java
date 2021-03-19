package com.swapServer.demo.ipAccuracy;

import com.swapServer.demo.Utils;
import com.swapServer.demo.ipAccuracy.impl.IpTaoBaoFilter;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @Data :  2021/3/19 17:26
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class IpAccuracyChain {

    private List<Site> sites = Lists.newArrayList();

    private static List<IpAccuracyFilter> ipAccuracyFilters = Lists.newArrayList();

    private int index = 0;//检测次数

    private boolean accurate;//是否准确 大于一半为准确

    static {
        ipAccuracyFilters.add(new IpTaoBaoFilter());
    }

    public void accuracy(String goalIp){
        if (index == ipAccuracyFilters.size() && index > 0) {
            //全部检测完毕，开始验证准确性
            Site localSite = Utils.getLionSouIP(goalIp);

            accurate = false;
        } if (ipAccuracyFilters.size() == 0 || index >= ipAccuracyFilters.size()) {
            return;
        } else {
            index ++ ;
            ipAccuracyFilters.get(index - 1).doFilter(goalIp, this);
        }
    }

    public void setSite(Site site) {
        sites.add(site);
    }

    public boolean isAccurate(){
        return accurate;
    }
}
