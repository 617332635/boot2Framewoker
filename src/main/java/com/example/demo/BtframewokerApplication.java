package com.example.demo;

import com.example.demo.Multitask_Parallel_Thread_Pool_Processing.ParallelThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration
public class BtframewokerApplication{

	public static void main(String[] args) throws  Exception{
		SpringApplication.run(BtframewokerApplication.class, args);
		List<String> arr=new ArrayList<String>();
		 new ParallelThreadPool(arr,5,1000).run();
	}

}

