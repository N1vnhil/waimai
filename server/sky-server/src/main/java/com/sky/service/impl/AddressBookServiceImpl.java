package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    public void add(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
    }

    public List<AddressBook> list() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder()
                .userId(userId)
                .build();
        return addressBookMapper.list(addressBook);
    }

    public AddressBook getDefault() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = AddressBook.builder()
                .userId(userId)
                .isDefault(1)
                .build();
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public void setDefault(Long id) {
        AddressBook addressBook = getDefault();
        if(addressBook != null) {
            addressBook.setIsDefault(0);
            addressBookMapper.update(addressBook);
        }

        addressBook = AddressBook.builder()
                .id(id)
                .isDefault(1)
                .build();
        addressBookMapper.update(addressBook);
    }

    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    public void delete(Long id) {
        addressBookMapper.deleteById(id);
    }

    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }
}


