package com.example.main;

import com.example.config.ProjectConfiguration;
import com.example.repositories.SheetmusicRepo;
import com.example.services.WsService;
import com.example.services.WsService.Song;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class SprongApplication {

	public static void main(String[] args) {
		try(var c = new AnnotationConfigApplicationContext(ProjectConfiguration.class)) {
            //SheetmusicRepo  sheetmusicRepo = c.getBean(SheetmusicRepo.class);
            //sheetmusicRepo.allSongs();  		//view all pieces currently in repo (has aspect to print songs)
            //sheetmusicRepo.addSong(Song song) //add a song to repo (automatically executed by WsService.defaultSearch())
            Scanner query = new Scanner(System.in);
            WsService wsService = c.getBean(WsService.class);

            String composerQuery = query.nextLine();
            String songQuery = query.nextLine();

            do {
                Object[] results = wsService.defaultSearch(composerQuery, songQuery);  //scrape for piece (has aspect for success and exception)
                Song result = (Song) results[0];
                if (result == null) {
                    System.out.println("No results found");
                    break;
                }
                wsService.downloadPDF(result);
				if ((boolean) results[1]) {
					System.out.println("this was the last result..");
					break;
				}
                System.out.println("Poor result? enter 'yes' to see next result");
            }
            while (query.nextLine().equalsIgnoreCase("yes"));

			/*to do: -done
					 -done
					 -done
					 -use regex to get composer and song name from url or at a point in scrape RATHER than name file w user's query
					 -first check database to see if the piece has already been scraped before, scrape if it hasn't
					 -learn springboot
			*/
        }
	}
}
