/*
 * Copyright (c) 2018 by Yann39.
 *
 * This file is part of MyCryptoBinder.
 *
 * MyCryptoBinder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCryptoBinder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyCryptoBinder. If not, see <http://www.gnu.org/licenses/>.
 */

package com.mycryptobinder.models;

import java.util.List;

public class PoloniexDepositsWithdrawals {

    private List<PoloniexDeposit> deposits;
    private List<PoloniexWithdrawal> withdrawals;

    public List<PoloniexDeposit> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<PoloniexDeposit> deposits) {
        this.deposits = deposits;
    }

    public List<PoloniexWithdrawal> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<PoloniexWithdrawal> withdrawals) {
        this.withdrawals = withdrawals;
    }
}