package com.driver.Repository;


import com.driver.DeliveryPartner;
import com.driver.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.List;

@Repository
public class OrderRepository {


    // list for order storing
    List<Order> listOfOrders = new ArrayList<>();

    // list of partnerId
    List<DeliveryPartner> listOfDeliveryPartner = new ArrayList<>();

    // to keep the count for unAssigned orders:

    Set<String> signedOrders = new HashSet<>();


    // order and partner Pair
    Map<String, LinkedHashSet<Order>> listOfOrderPartnerPair = new HashMap<>();

    public void addOrder(Order order){
        listOfOrders.add(order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        listOfDeliveryPartner.add(partner);
    }


    public void addOrderPartnerPair(String orderId, String partnerId) {

        // increase the order count for the particular partnerId
        for(DeliveryPartner partner : listOfDeliveryPartner){
            if(Objects.equals(partnerId, partner.getId())){
                int numberOfOrders = partner.getNumberOfOrders();
                numberOfOrders++;
                partner.setNumberOfOrders(numberOfOrders);
            }
        }

        signedOrders.add(orderId);     /// this will keep count of unassigned orders

        // this line validate the number of orders added to the each partner
        // validate the duplicate objects:

        LinkedHashSet<Order> orderr  =  listOfOrderPartnerPair.getOrDefault(partnerId, new LinkedHashSet<>());

        // traverse the listOfOrders

        for(Order data : listOfOrders){
            if(Objects.equals(data.getId(), orderId)){
                orderr.add(data);
            }
        }

        // here we need hashMap to store the pair with partner id:
        listOfOrderPartnerPair.put(partnerId, orderr);
                                   // key     // value
    }

    public Order getOrderById(String orderId) {
        // linear search

        Order orderAns = null;
        for(Order order: listOfOrders){
            if(Objects.equals(order.getId(), orderId)){
                orderAns =  order;
                break;
            }
        }
        return orderAns;
    }

    public DeliveryPartner getPartnerById(String partnerId) {

        // linear search

        for(DeliveryPartner partner : listOfDeliveryPartner){
            if(Objects.equals(partnerId, partner.getId())){
                return partner;
            }
        }
        return null;
    }

    public Integer getOrderCountByPartnerId(String partnerId) {

        // linear search

        for(DeliveryPartner partner : listOfDeliveryPartner){
            if(Objects.equals(partner.getId(), partnerId)){
                return partner.getNumberOfOrders();
            }
        }

        // if no possible match:
        return null;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        // the required format is string and not in object:

        // now we traverse the hashMap

        // we need to return the list of order in string format:
        List<String> ordersOfPartnerId = new ArrayList<>();

        Set<Order> uniqueOrderOfPartner= new HashSet<>();

        for(Map.Entry<String, LinkedHashSet<Order>> data : listOfOrderPartnerPair.entrySet()){
            if(Objects.equals(data.getKey(), partnerId)){
                uniqueOrderOfPartner = data.getValue();
                break;
            }
        }

        for(Order order : uniqueOrderOfPartner){
            ordersOfPartnerId.add(order.getId());
        }

        return ordersOfPartnerId;

    }

    public List<String> getAllOrders() {

        List<String> listOfordersInString =  new ArrayList<>();

        for(Order order : listOfOrders){
            listOfordersInString.add(order.getId());
        }
        return listOfordersInString;
    }

    public Integer getCountOfUnassignedOrders() {

        // the logic is if the pair id is not match with original order then count it
        int unAssignedCount = listOfOrders.size() - signedOrders.size();


        return unAssignedCount;


    }

    public void deletePartnerById(String partnerId) {
        // remove the partner from original list and from the hashMap also

        DeliveryPartner partner = null;

        for(DeliveryPartner partner1 : listOfDeliveryPartner){
            if(Objects.equals(partner1.getId(), partnerId)){
                partner = partner1;
                break;
            }
        }
        // first to remove in original list:
        listOfDeliveryPartner.remove(partner);

        // now we remove it in hashmap also
        listOfOrderPartnerPair.remove(partnerId);

        // make that partner id orederCount is zero:


    }

    public void deleteByOrderId(String orderId) {

        // first is to delete in original list:

        Order order = null;

        for(Order order1 : listOfOrders){
            if(Objects.equals(order1.getId(), orderId)){
                order = order1;
                break;
            }
        }

        listOfOrders.remove(order);

        // now remove it from the assigned order of that partnerId
        // traverse the map and check each value for the given orderId:

        for(Map.Entry<String, LinkedHashSet<Order>> data: listOfOrderPartnerPair.entrySet()){
            // now get the value and check it in set if present then remove it:
//            Set<Order> newSet = data.getValue();

            // now check the orderid in this set:

            data.getValue().removeIf(order2 -> Objects.equals(order2.getId(), orderId));
        }


    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        // return the last added time in the partner id assigned in map:

        // traverse the map
        int time = 0;
        for(Map.Entry<String, LinkedHashSet<Order>> data:  listOfOrderPartnerPair.entrySet()){
            if(Objects.equals(data.getKey(), partnerId)){
                for(Order order1 : data.getValue()){
                    time = order1.getDeliveryTime();
                }
                break;
            }
        }

        // return in hour format:

        return String.valueOf(time/60)+":"+ String.valueOf(time%60);


    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        // traverse the map
        int count = 0;
        int hours = Integer.parseInt(time.substring(0,2));
        int minutes = Integer.parseInt(time.substring(3,time.length()));
        int givenTime = (hours*60)+minutes;


        for(Map.Entry<String, LinkedHashSet<Order>> data : listOfOrderPartnerPair.entrySet()){
            if(Objects.equals(partnerId, data.getKey())){
                // now traverse the set after the given time:
                for(Order order1: data.getValue()){
                    if(order1.getDeliveryTime() >= givenTime){
                        count++;
                        count = data.getValue().size() - count;
                        break;
                    }else{
                        count++;
                    }
                }
            }
        }

        return count;
    }
}
