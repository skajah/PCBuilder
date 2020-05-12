package com.company.PCBuilder.interfaces;

import com.company.PCBuilder.PCComponent;

public interface OnDetailChosenListener {
    String getFragmentName();
    void onDetailButtonClicked(PCComponent component);
}
