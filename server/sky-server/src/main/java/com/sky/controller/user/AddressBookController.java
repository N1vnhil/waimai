package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/addressBook")
@Api("地址簿相关接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("新增地址簿, {}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        log.info("查询用户所有地址");
        List<AddressBook> addressBooks = addressBookService.list();
        return Result.success(addressBooks);
    }

    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        log.info("查询默认地址");
        AddressBook addressBook = addressBookService.getDefault();
        return Result.success(addressBook);
    }

    @PutMapping("/default")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        Long id = addressBook.getId();
        log.info("设置默认地址：{}", id);
        addressBookService.setDefault(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("根据id查询地址：{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    @DeleteMapping
    public Result delete(Long id) {
        log.info("根据id删除：{}", id);
        addressBookService.delete(id);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("更新地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }
}
