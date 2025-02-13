package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import org.apache.poi.ss.formula.functions.Address;

import java.util.List;

public interface AddressBookService {

    public void add(AddressBook addressBook);

    public List<AddressBook> list();

    public AddressBook getDefault();

    public void setDefault(Long id);

    public AddressBook getById(Long id);

    public void delete(Long id);

    public void update(AddressBook addressBook);
}
