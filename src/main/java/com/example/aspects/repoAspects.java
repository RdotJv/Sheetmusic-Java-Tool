package com.example.aspects;

import com.example.services.WsService.Song;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Aspect
@Component
public class repoAspects {
    @Around("execution(* com.example.repositories.SheetmusicRepo.allSongs(..))")
    public void aroundAllSong(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("All pieces currently in repo");
        ArrayList<Song> result = (ArrayList<Song>) joinPoint.proceed();
        result.forEach(System.out::println);
    }

    @Around("execution(* com.example.repositories.SheetmusicRepo.addSong(..))")
    public void aroundAddSong(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Attempting to add " + joinPoint.getArgs()[0]);
        try{
            joinPoint.proceed();
            System.out.println("Success!");
        }
        catch (Exception e) {
            System.out.println("Possibly failed to add to repository "+e);
        }
    }
}
