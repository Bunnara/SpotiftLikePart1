import java.io.*;
import java.util.*;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jl.decoder.*;
import javazoom.jl.player.*;



class PlayAudioFile extends Thread {
   public File[] arrayOfMusic;
   jlp music = null;
   int cMusicIndex = 0;
   public PlayAudioFile() {
	 try {
       File musicFiles = new File("./audio");
       this.arrayOfMusic = musicFiles.listFiles();
      } catch(Exception e) {
		  System.out.println(e);
      }
   }

   public void run() {
      System.out.println("Do you Like the song?");
      Scanner scanner = new Scanner(System.in);
      String likeSong = scanner.nextLine();
      if(likeSong.contains("yes")) {
		  System.out.println("You like this song.");
	  }
   }

   public void getInfo(int musicIndex) {
	 try {
	 cMusicIndex = musicIndex;
	 this.music = new jlp((this.arrayOfMusic[cMusicIndex]).getPath());
	 Mp3File mp3file = new Mp3File((this.arrayOfMusic[cMusicIndex]).getPath());
	 ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	 System.out.println("Title: " + id3v2Tag.getTitle());
	 System.out.println("Artist: " + id3v2Tag.getArtist());
	 System.out.println("Year: " + id3v2Tag.getYear());
	 System.out.println("Genre: " + id3v2Tag.getGenre());
	 System.out.println("File Path: " + this.arrayOfMusic[cMusicIndex].getPath());
	 } catch(IOException e) {
	   e.printStackTrace();
	 } catch(UnsupportedTagException e) {
	   e.printStackTrace();
	 } catch(InvalidDataException e) {
	   e.printStackTrace();
	 }
   }
   public void play(int musicIndex) {
	 this.getInfo(musicIndex);
	 try {
	    this.music.play();
	 } catch(JavaLayerException e) {
	    e.printStackTrace();
	 }
   }

   public int findIndexName(String name) {
      int fileIndex = 9;
      for(int i = 0; i < arrayOfMusic.length; i++) {
		  if(((arrayOfMusic[i]).getName().replace(".mp3", "")).equals(name)) {
			   fileIndex = i;
		  }
	  }
      return fileIndex;
   }
   public void displayMusicNames() {
	   for(int i = 0; i < arrayOfMusic.length; i++) {
	   	 System.out.println((arrayOfMusic[i]).getName().replace(".mp3", ""));
	  }
   }
}

class LoadTextFromFile {
   char[] textToBeRead = new char[1050];
   public LoadTextFromFile() {
	  try {
        FileReader textFile = new FileReader("./txt/title_and_menus.txt");
        textFile.read(this.textToBeRead, 0, 1050);
        textFile.close();
	  } catch(Exception e) {
		  System.out.println(e);
	  }
   }
   public void outputMenus() {
      System.out.println(this.textToBeRead);
   }
}

class HomeMenu {
  public HomeMenu() {
     PlayAudioFile music = new PlayAudioFile();
     music.displayMusicNames();
     System.out.println("Do you want to add a song to the Library?");
     try {
	   Scanner input = new Scanner(System.in);
	   FileWriter libraryFile = new FileWriter("./library.txt");
	   String line = "";
	   while(!line.contains("quit")) {
		  line.replace("quit;", "");
		  libraryFile.write(line);
		  line = input.nextLine() + ";";
       }
       libraryFile.close();
     } catch(IOException e) {
		 e.printStackTrace();
	 }
  }
}
class SearchByTitleMenu {
   public SearchByTitleMenu() {

   }
   public void printRequest() {
	   System.out.println("Please Enter a Song By Title:");
   }
   public void searchSong() {
      PlayAudioFile paf = new PlayAudioFile();
      Scanner input = new Scanner(System.in);
      String musicName = input.nextLine();
      paf.start();
      paf.play(paf.findIndexName(musicName));
   }
}
class LibraryMenu {
    public LibraryMenu() throws IOException {
	  PlayAudioFile paf = new PlayAudioFile();
      FileReader libraryFile = new FileReader("./library.txt");
      char[] textToBeRead = new char[1000];
      libraryFile.read(textToBeRead, 0, 1000);
      String textFromFile = "";
      for(char eachChar: textToBeRead) {
		  textFromFile += eachChar;
	  }
      String[] arrayOfMusicInLibrary = textFromFile.split(";");
      for(short i = 0; i < arrayOfMusicInLibrary.length; i++) {
		System.out.println(paf.findIndexName(arrayOfMusicInLibrary[i]) + ") " + arrayOfMusicInLibrary[i]);
	  }
      libraryFile.close();
      System.out.println("Select a song to play by number: ");
      Scanner scanner = new Scanner(System.in);
      short num = scanner.nextShort();
      scanner.close();
      paf.play(num);
	}
}

class FrontPage {
	public FrontPage() {

	}
	public void nextScreen() {
       Runtime process = Runtime.getRuntime();
       try {
       new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
       } catch(IOException e) {
		   e.printStackTrace();
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   }
	}
	public void display() {
	  LoadTextFromFile txt = new LoadTextFromFile();
      txt.outputMenus();
      InputStream is = System.in;
      Scanner inputReader = new Scanner(is);
	  String menuLetter = inputReader.next();
      switch(menuLetter) {
		case "h":
		    nextScreen();
		    HomeMenu home = new HomeMenu();
		    break;
		case "s":
		    nextScreen();
		    SearchByTitleMenu musicTitle = new SearchByTitleMenu();
		    musicTitle.printRequest();
		    musicTitle.searchSong();
		    break;
		case "l":
		    nextScreen();
		    try {
		      LibraryMenu libMenu = new LibraryMenu();
		    } catch(IOException e) {
				e.printStackTrace();
			}
		    break;
		case "q":
		    Runtime process = Runtime.getRuntime();
			try {
			    new ProcessBuilder("cmd", "/c", "exit").inheritIO().start().waitFor();
			} catch(IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
	        }
		    break;
	  }
	}
}

class SpotifyLike {
	public static void main(String[] args) {
       FrontPage fpg = new FrontPage();
       fpg.display();
	}
}