package com.mnu.sosm.Criteria;

import com.mnu.sosm.entity.DutyInfo;
import com.mnu.sosm.repository.ICustomRepository;
import com.mnu.sosm.utils.PageModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DutyCriteria extends Criteria<DutyInfo> {


    private String dutyDate;//名称模糊匹配

    public DutyCriteria(){
        super(DutyInfo.class);
    }

    @Override public String createQuery() {
        if (isNativeQuery()){
            String sql = "SELECT s.* " + sql() + " ORDER BY s.start_time,daily_order";
            return sql;
        }else {
            String sql = "SELECT s " + sql() + " ORDER BY s.createTime , dailyOrder";
            return sql;
        }
    }
    @Override public String createCount() {

        return "select count(1) " + sql();
    }

    @Override
    public Map<String, Object> param() {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.hasLength(dutyDate)) {
            param.put("dutyTime", escapeLike(dutyDate));
        }
        return param;
    }

    public String sql() {
        StringBuilder sql = new StringBuilder();
        if(isNativeQuery()){
            sql.append(" from mnu_sosm_duty s WHERE 1=1 ");
            if (StringUtils.hasLength(dutyDate)){
                sql.append(" and s.duty_date like :dutyDate");
            }
        }else {
            sql.append(" from DutyInfo s WHERE 1=1 ");
            if (StringUtils.hasLength(dutyDate)){
                sql.append(" and s.dutyDate like :dutyDate");
            }
        }

        return sql.toString();


    }

    public PageModel findByCriteria(ICustomRepository<?, ?> dao){
        Criteria c = doSearch(dao);
        PageModel ret = new PageModel();
        BeanUtils.copyProperties(c,ret);

        return ret;
    }

}
