package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User user1=userRepository.save(user);
        return user1.getAge();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries>webSeriesList=webSeriesRepository.findAll();
        User user=userRepository.findById(userId).get();

        int cnt=0;

        for(WebSeries webSeries : webSeriesList){
            if(user.getAge()<=webSeries.getAgeLimit() && user.getSubscription().getSubscriptionType().equals(webSeries.getSubscriptionType())){
                cnt++;
            }
        }
        return cnt;
    }


}
