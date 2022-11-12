package org.example;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.config.EntityState;
import org.example.config.RepositoryTemplateFactory;
import org.example.domain.Attendance;
import org.example.domain.Performance;
import org.example.domain.Sell;
import org.example.domain.User;
import org.example.dto.AttendanceDTO;
import org.example.repository.AttendanceRepository;
import org.example.repository.SellRepository;
import org.example.repository.UserRepository;
import org.example.vo.SellDetailVO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class MyController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    @GetMapping("/users")
    public R<List<User>> getUsers(){
        List<User> all = RepositoryTemplateFactory.getJpaRepository(UserRepository.class)
                .findAll();
        return R.ok(all);
    }






    @PostMapping("/attendances")
    public void createAttendance(@RequestBody(required = false) AttendanceDTO dto){
        Integer month;
        Integer year;
        //直接生成上个月的
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        month = lastMonth.getMonthValue();
        year = lastMonth.getYear();

        List<Attendance> list = RepositoryTemplateFactory.getJpaRepository(AttendanceRepository.class)
                .findByYearAndMonthAndEntityState(year, month, EntityState.NORMAL);

        if (CollectionUtils.isEmpty(list)) {
            List<User> users = new ArrayList<>();
            //空的时候去查没有离职的，以及上个月离职的
            List<User> userList = RepositoryTemplateFactory.getJpaRepository(UserRepository.class)
                    .findByEntityState(EntityState.NORMAL);

            LocalDate start = LocalDate.of(year, month, 1);
            LocalDate end = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
            List<User> leaveUsers = RepositoryTemplateFactory.getJpaRepository(UserRepository.class)
                    .findByDepartureTimeBetween(start, end);

            users.addAll(userList);
            users.addAll(leaveUsers);

            for (User user : users) {
                Attendance attendance = new Attendance(year, month, user);
                RepositoryTemplateFactory.getJpaRepository(AttendanceRepository.class)
                        .save(attendance);
            }
        }
    }


    @PostMapping("/upload/social-security")
    public void socialSecurity(@RequestParam MultipartFile uploadFile,
                               @RequestParam(required = false) String date) {

        if (uploadFile == null || uploadFile.isEmpty())
            return;

        LocalDate now = LocalDate.now();

        LocalDate localDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusDays(1);

        List<Attendance> list = RepositoryTemplateFactory.getJpaRepository(AttendanceRepository.class)
                .findByYearAndMonthAndEntityState(localDate.getYear(), localDate.getMonthValue(), EntityState.NORMAL);


        try {
            InputStream inputStream = uploadFile.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);

            List<Object> idCards = reader.readColumn(1, 1);
            List<Object> moneyList = reader.readColumn(49, 1);

            for (int i = 0; i < idCards.size(); i++) {
                String idCard = (String)idCards.get(i);
                Optional<Attendance> first = list.stream().filter(attendance -> {
                    return attendance.getIdCard().equals(idCard);
                }).findFirst();
                if (first.isPresent()) {
                    Attendance attendance = first.get();
                    Double o = (Double)moneyList.get(i);
                    attendance.setSocialSecurityCharge(new BigDecimal(o));
                    RepositoryTemplateFactory.getJpaRepository(AttendanceRepository.class)
                            .save(attendance);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @PostMapping("/upload/provident-charge")
    public void providentCharge(@RequestParam MultipartFile uploadFile, @RequestParam(required = false) String date) {

        if (uploadFile == null || uploadFile.isEmpty())
            return;

        LocalDate now = LocalDate.now();

        LocalDate localDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusDays(1);

        List<Attendance> list = RepositoryTemplateFactory.getJpaRepository(AttendanceRepository.class)
                .findByYearAndMonthAndEntityState(localDate.getYear(), localDate.getMonthValue(), EntityState.NORMAL);


        try {
            InputStream inputStream = uploadFile.getInputStream();
            ExcelReader reader = ExcelUtil.getReader(inputStream);

            List<Object> idCards = reader.readColumn(3, 1);
            List<Object> moneyList = reader.readColumn(7, 1);

            for (int i = 0; i < idCards.size(); i++) {
                String idCard = (String)idCards.get(i);
                Optional<Attendance> first = list.stream().filter(attendance -> {
                    return attendance.getIdCard().equals(idCard);
                }).findFirst();
                if (first.isPresent()) {
                    Attendance attendance = first.get();
                    String o = (String)moneyList.get(i);
                    attendance.setProvidentCharge(new BigDecimal(o));
                    RepositoryTemplateFactory.getJpaRepository(AttendanceRepository.class)
                            .save(attendance);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @GetMapping("/data/sells")
    public R<List<SellDetailVO>> getSell(@RequestParam(required = false) String date){

        Integer year;
        Integer month;

        //拿到用户去sell里面查
        if (StringUtils.hasLength(date)) {
            String[] split = date.split("-");
            year = new Integer(split[0]);
            month = new Integer(split[1]);
        } else {
            LocalDate now = LocalDate.now();
            LocalDate localDate = now.minusMonths(1);
            year = localDate.getYear();
            month = localDate.getMonthValue();
        }

        List<SellDetailVO> list = new ArrayList<>();



        List<User.UserType> userTypes = Arrays.asList(User.UserType.USER_DELIVERY,
                User.UserType.USER_MANAGER, User.UserType.MASTER);

        //拿到所有的用户主理，根据组长分组
        List<User> users = RepositoryTemplateFactory.getJpaRepository(UserRepository.class)
                .findByUserTypeInAndEntityState(userTypes, EntityState.NORMAL);

        //根据组长分组。仅限于用户主理的
        List<User> manager = users.stream().filter(user -> {
            return user.getUserType().equals(User.UserType.USER_MANAGER);
        }).collect(Collectors.toList());

        //直接交付专员拿出来
        List<User> delivery = users.stream().filter(user -> {
            return user.getUserType().equals(User.UserType.USER_DELIVERY);
        }).collect(Collectors.toList());

        fillUsesr(list, manager, year, month);
        fillDelivery(list, delivery, year, month);

        return R.ok(list);
    }

    private void fillDelivery(List<SellDetailVO> list, List<User> delivery, Integer year, Integer month) {
        List<SellDetailVO.SellDetail> detailList = new ArrayList<>();
        SellDetailVO sellDetailVO = new SellDetailVO();
        sellDetailVO.setManagerName("交付中心");
        sellDetailVO.setList(detailList);
        list.add(sellDetailVO);
        for (User user : delivery) {
            fill(user, year, month, 0, detailList);
        }
    }

    private void fillUsesr(List<SellDetailVO> list, List<User> manager, Integer year, Integer month) {
        Map<User, List<User>> listMap = manager.stream().collect(Collectors.groupingBy(User::getMaster));

        listMap.forEach((k, y) -> {
            //算出y里面,所有的手底下的员工的交车和，用来算底薪
            Integer totalLockNumber = y.stream().map(user -> {
                Sell sell = RepositoryTemplateFactory.getJpaRepository(SellRepository.class)
                        .findByUserAndYearAndMonth(k, year, month);
                if (sell != null) {
                    return sell.getLockNumber();
                }
                return 0;
            }).reduce(0, Integer::sum);

            List<SellDetailVO.SellDetail> detailList = new ArrayList<>();
            SellDetailVO sellDetailVO = new SellDetailVO();
            sellDetailVO.setManagerName(k.getName());
            sellDetailVO.setList(detailList);

            list.add(sellDetailVO);

            fill(k, year, month, totalLockNumber, detailList);

            //组长下的员工
            for (User user : y) {
                fill(user, year, month, totalLockNumber, detailList);
            }
        });
    }


    private void fill(User k, Integer year, Integer month, Integer totalLockNumber,
                                        List<SellDetailVO.SellDetail> detailList) {
        //组长
        Sell sell = RepositoryTemplateFactory.getJpaRepository(SellRepository.class)
                .findByUserAndYearAndMonth(k, year, month);
        if (sell != null) {
            SellDetailVO.SellDetail sellDetail = new SellDetailVO.SellDetail();
            sellDetail.setName(k.getName());
            Integer lockNumber = sell.getLockNumber();
            sellDetail.setLockNumber(lockNumber);
            BigDecimal ladder = Performance.getLadder(User.UserType.USER_MANAGER, sell.getLockNumber());
            sellDetail.setLadder(ladder);
            //锁单提成 = 用阶梯 x 锁单数 x 0.5
            BigDecimal lockMoney = ladder.multiply(new BigDecimal(lockNumber)).multiply(new BigDecimal("0.5"));
            sellDetail.setLockMoney(lockMoney);
            Integer deliveryNumber = sell.getDeliveryNumber();
            sellDetail.setDeliveryNumber(deliveryNumber);

            //当月交车提成 = 交车数量 x (阶梯 -> 上面算出来的阶梯) x 0.5
            BigDecimal deliveryMoney = new BigDecimal(deliveryNumber).multiply(ladder).multiply(new BigDecimal("0.5"));
            sellDetail.setDeliveryMoney(deliveryMoney);

            int notDeliveryNumber = sell.getLockNumber() - sell.getDeliveryNumber();
            sellDetail.setNotDeliveryNumber(notDeliveryNumber);

            //订单提成 = 锁单提成 + 当月交车提成
            BigDecimal orderMoney = lockMoney.add(deliveryMoney);
            sellDetail.setOrderMoney(orderMoney);

            sellDetail.setDirectCarSellPrice(sell.getDirectCarSellPrice());

            sellDetail.setPrevDeliveryNumber(sell.getPrevDeliveryNumber());
            BigDecimal prevDeliveryMoney = sell.getPrevDeliveryMoney();
            sellDetail.setPrevDeliveryMoney(prevDeliveryMoney);

            BigDecimal deductMoney = orderMoney.add(sell.getDirectCarSellPrice()).add(prevDeliveryMoney);
            sellDetail.setTotalDeductMoney(deductMoney);

            //组长 = 底薪 + 组内锁单数阶梯
            BigDecimal baseMoney = k.getBaseMoney();
            if (sell.getUser().getUserType().equals(User.UserType.MASTER)) {
                BigDecimal baseLadder = Performance.getLadder(User.UserType.MASTER, totalLockNumber);
                baseMoney = baseMoney.add(baseLadder);
            }
            sellDetail.setBaseMoney(baseMoney);

            sellDetail.setTotalMoney(deductMoney.add(baseMoney));

            detailList.add(sellDetail);
        }
    }



}
