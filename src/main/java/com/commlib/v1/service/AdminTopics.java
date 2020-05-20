package com.commlib.v1.service;

@Topics.WithPrefix("admin")
public interface AdminTopics extends Topics.Topic {

    @Topics.WithTopic("SignUp")
    int signUp = 1;

    @Topics.WithTopic("SignIn")
    int signIn = 2;

}
