package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){


        /*  Question given-----
            There are 2 types of facilities that are available (Webseries and match) ,
            some web series are available with BASIC plan and some are available with PRO plan only.
            To see matches you need to have the ELITE subscription. If you have an ELITE subscription you
            can watch any match or web series. If you have PRO subscription you can watch the PRO web series
            as well as those of BASIC plan webseries.

            The subscription costing goes something like this. There is only monthly subscription possible :

            For Basic Plan : 500 + 200noOfScreensSubscribed
            For PRO Plan : 800 + 250noOfScreensSubscribed
            For ELITE Plan : 1000 + 350*noOfScreensSubscribed
         */

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        Subscription subscription=new Subscription();
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setTotalAmountPaid(totalSubscriptionAmount(subscriptionEntryDto.getSubscriptionType(),
                                                                subscriptionEntryDto.getNoOfScreensRequired()));
        subscription.setStartSubscriptionDate(new Date());

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);
        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        /*
            In this function you need to upgrade the subscription to  its next level
            ie if You are A BASIC subscriber update to PRO and if You are a PRO upgrade to ELITE.
            Incase you are already an ELITE member throw an Exception
            and at the end return the difference in fare that you need to pay to get this subscription done.
        */

        User user=userRepository.findById(userId).get();
        Subscription currSub=user.getSubscription();
        int amountNeedToPaid=0;
        if(currSub.getSubscriptionType().equals(SubscriptionType.BASIC)){
            int totalAmount=totalSubscriptionAmount(SubscriptionType.PRO,currSub.getNoOfScreensSubscribed());
            int alreadyPaid=totalSubscriptionAmount(SubscriptionType.BASIC,currSub.getNoOfScreensSubscribed());
            currSub.setSubscriptionType(SubscriptionType.PRO);
            currSub.setTotalAmountPaid(totalAmount);
            amountNeedToPaid=totalAmount-alreadyPaid;
        }
        else if(currSub.getSubscriptionType().equals(SubscriptionType.PRO)){
            int totalAmount=totalSubscriptionAmount(SubscriptionType.ELITE,currSub.getNoOfScreensSubscribed());
            int alreadyPaid=totalSubscriptionAmount(SubscriptionType.PRO,currSub.getNoOfScreensSubscribed());
            currSub.setSubscriptionType(SubscriptionType.ELITE);
            currSub.setTotalAmountPaid(totalAmount);
            amountNeedToPaid=totalAmount-alreadyPaid;
        }else if(currSub.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        userRepository.save(user);
        return amountNeedToPaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){



        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription>subscriptions=subscriptionRepository.findAll();
        int revenue=0;
        for(Subscription subscription : subscriptions){
            revenue+=subscription.getTotalAmountPaid();
        }
        return revenue;
    }


    private int totalSubscriptionAmount(SubscriptionType type,int noOfScreen){
        if(type.equals(SubscriptionType.BASIC)){
            return 500+200*noOfScreen;
        }else if(type.equals(SubscriptionType.PRO)){
            return 800+250*noOfScreen;
        }else if(type.equals(SubscriptionType.ELITE)){
            return 1000+350*noOfScreen;
        }
        else {
            return 0;
        }
    }

}
