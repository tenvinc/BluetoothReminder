package com.project.tenvinc.bluetoothreminder.interfaces;

import java.util.List;

public interface IListListener<E> {

    void trigger(List<E> list);
}
