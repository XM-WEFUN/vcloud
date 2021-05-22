package com.bootvue.core.ddo.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActionItem {
    // label:查看  value:"user:add,user:list" || "list"
    private String label;
    private String value;
}
