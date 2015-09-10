package cf.study.testing.bdd.example;
//package com.c0deattack.cucumberjvmtutorial;

import org.junit.runner.RunWith;

import cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(format={"pretty", "html:target/cucumber"}, features= {"deposit.feature"})
public class RunTests {
}
