import java.io.*;
import java.util.*;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javazoom.jl.player.advanced.*;
import javazoom.jl.player.advanced.jlap.*;

class IncrementTimerEverySec extends TimerTask {
	public void run() {
		if(PlayAudioFile.pauseFlag == true) {
          PlayAudioFile.currentLength = PlayAudioFile.currentLength;
		} else {
		  PlayAudioFile.currentLength += 30;
	    }
	}
}

class PlayAudioFile extends AdvancedPlayer implements Runnable {
   public File[] arrayOfMusic;
   AdvancedPlayer ap;
   static int songId = -1;
   jlap music = null;
   int cMusicIndex = 0;
   int songPos = 0;
   static boolean pauseFlag = false;
   static int currentLength = 0;
   static FileInputStream fis;
   static AdvancedPlayer klassAdPlay = null;
   static char[] favoriteList = new char[1050];

   public PlayAudioFile(InputStream stream) throws JavaLayerException {
	   super(stream);
   }
   public PlayAudioFile(InputStream stream, AudioDevice device) throws JavaLayerException {
	   super(stream, device);
   }

   public PlayAudioFile() throws FileNotFoundException, JavaLayerException {
      super(new FileInputStream("audio/Aglow Hollow - Winnowing Moon (live).mp3"));
   }

   public void init() {
	   try {
	     File musicFiles = new File("./audio");
	     this.arrayOfMusic = musicFiles.listFiles();
	   } catch(Exception e) {
	   	 System.out.println(e);
      }
   }

   public void run() {
	   try {
		 if(PlayAudioFile.pauseFlag == true) {
		   pauseCurrently();
		 } else {
           playCurrently();
	     }
	   } catch(JavaLayerException e) {
		 e.printStackTrace();
	   }
   }

   public void pauseCurrently() throws JavaLayerException {
	     klassAdPlay = this.playMusic(songId, PlayAudioFile.currentLength);
	     klassAdPlay.play(PlayAudioFile.currentLength, PlayAudioFile.currentLength);
   }

   public void playCurrently() throws JavaLayerException {
	     klassAdPlay = this.playMusic(songId, PlayAudioFile.currentLength);
	     klassAdPlay.play(PlayAudioFile.currentLength, 120000);
   }

   public static void destroyMusicalThread(Thread k) {
      if((k.getName() == "music")) {
		  k.stop();
	  }
   }

   public void promptWhenPlay() throws JavaLayerException, InterruptedException, IOException {
	  System.out.println("Do you Like the song?");
	  Scanner scanner = new Scanner(System.in);
	  String likeSong = scanner.nextLine();
	   if(likeSong.contains("yes")) {
	   	   System.out.println("You like this song.");
	   } else if(likeSong.contains("Forward")) {
		   PlayAudioFile.currentLength += 150;
		   screenRefresh();
		   System.out.println("Fast Forwarding");
   	  } else if(likeSong.contains("Rewind")) {
		   PlayAudioFile.currentLength -= 150;
		   screenRefresh();
		   System.out.println("Rewinding");
   	  } else if(likeSong.contains("Pause")) {
		   PlayAudioFile.pauseFlag = true;
		   screenRefresh();
		   System.out.println("Pausing");
   	  } else if(likeSong.contains("Play")) {
		   PlayAudioFile.pauseFlag = false;
		   screenRefresh();
		   System.out.println("Playing");
   	  }  else if(likeSong.contains("Favorite")) {
		   favoriteChoice(true);
		   screenRefresh();
		   System.out.println("You Favorite this song.");
   	  }  else if(likeSong.contains("Unfavorite")) {
		   favoriteChoice(false);
           screenRefresh();
		   System.out.println("You Un-Favorite this song.");
   	  } else if(likeSong.contains("back")) {
		  try {
			 screenRefresh();
   	         FrontPage fpg = new FrontPage();
   	         fpg.display();
   	       } catch(IOException e) {
		   	 e.printStackTrace();
		   } catch(InterruptedException e) {
		   	 e.printStackTrace();
	       }
   	  } else {
		   screenRefresh();
	  }
   }

   public void screenRefresh() throws InterruptedException, IOException {
	    Map<Thread, StackTraceElement[]> collectionOfThreads = Thread.getAllStackTraces();
	    collectionOfThreads.forEach((k, v) -> destroyMusicalThread(k));
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
   }

   public void favoriteChoice(boolean flag) throws IOException {
	        String nameOfMusic = this.arrayOfMusic[cMusicIndex].getPath().replace(".mp3", "").replace(".\\audio\\", "");
	        String newFavoriteList = "";
	        String numWriteToFile = "";
	        numWriteToFile = String.valueOf(cMusicIndex) + "]" + nameOfMusic + "\n";
			newFavoriteList += numWriteToFile;
			if(flag == false) {
				newFavoriteList = (new String(favoriteList)).replace(numWriteToFile, "");
			}
            FileWriter fileW = new FileWriter("./audio/favorite/fav.txt");
			fileW.write(newFavoriteList);
	   	    fileW.close();
   }

   public void getInfo(int musicIndex) {
	 try {
	 FileReader fileR = new FileReader("./audio/favorite/fav.txt");
	 fileR.read(favoriteList, 0, 1050);
	 fileR.close();
	 cMusicIndex = musicIndex;
	 this.music = new jlap();
	 Mp3File mp3file = new Mp3File((this.arrayOfMusic[cMusicIndex]).getPath());
	 ID3v2 id3v2Tag = mp3file.getId3v2Tag();
	 System.out.println("Title: " + id3v2Tag.getTitle());
	 System.out.println("Artist: " + id3v2Tag.getArtist());
	 System.out.println("Year: " + id3v2Tag.getYear());
	 System.out.println("Genre: " + id3v2Tag.getGenre());
	 System.out.println("File Path: " + this.arrayOfMusic[cMusicIndex].getPath());
	 Pattern musicNum = Pattern.compile(String.valueOf(cMusicIndex) + "]" + this.arrayOfMusic[cMusicIndex].getPath().replace(".mp3", "").replace(".\\audio\\", ""), Pattern.CASE_INSENSITIVE);
	 Matcher favFlagTrue = musicNum.matcher(new String(favoriteList));
	 boolean favFound = favFlagTrue.find();
	 if(Boolean.valueOf(favFound).equals(Boolean.TRUE)) {
	     System.out.println("isFavorite: true");
	 } else if(Boolean.valueOf(favFound).equals(Boolean.FALSE)){
		 System.out.println("isFavorite: false");
     }
	 } catch(IOException e) {
	   e.printStackTrace();
	 } catch(UnsupportedTagException e) {
	   e.printStackTrace();
	 } catch(InvalidDataException e) {
	   e.printStackTrace();
	 }
   }
   public AdvancedPlayer playMusic(int musicIndex, int start) {
	 this.getInfo(musicIndex);
	 try {
		PlayAudioFile.fis = new FileInputStream(this.arrayOfMusic[cMusicIndex].getPath());
	    this.ap = new AdvancedPlayer(PlayAudioFile.fis);
	 } catch(JavaLayerException e) {
	    e.printStackTrace();
	 } catch(IOException e) {
		e.printStackTrace();
	 }
	 return this.ap;
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
     System.out.println("Do you want to add a song to the Library?");
     try {
       PlayAudioFile music = new PlayAudioFile();
       music.init();
       music.displayMusicNames();
	   MusicInfo musicInfo = new MusicInfo();
	   FileWriter libraryFile = new FileWriter("./library.txt");
	   String line = "";
	   while(!line.contains("done")) {
		  libraryFile.write(line);
		  line = String.valueOf(musicInfo.getIndexOfMusic(true, null)) + ") " + musicInfo.musicName + "\n";
		  if(line.contains("done")) {
		  line.replace("done", "");
		  libraryFile.close();
          FrontPage fpg = new FrontPage();
            try {
              fpg.display();
              fpg.nextScreen();
		    } catch(InterruptedException e) {
				e.printStackTrace();
		    }
		}
       }
     } catch(IOException e) {
		 e.printStackTrace();
	 } catch(JavaLayerException e) {
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

   public void searchSong() throws JavaLayerException, FileNotFoundException, InterruptedException, IOException {
	 MusicInfo musicInfo = new MusicInfo();
     int indexOfAudioFile = musicInfo.getIndexOfMusic(true, null);
     SelectMusic selectedMusic = new SelectMusic();
     selectedMusic.playSelected(indexOfAudioFile);
   }
}

class SelectMusic {
	public SelectMusic() throws InterruptedException, IOException {
       new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}
	public void playSelected(int id) throws JavaLayerException, FileNotFoundException, InterruptedException, IOException {
        do {
	     Timer timer = new Timer();
	     timer.schedule(new IncrementTimerEverySec(), 1000, 1000);
		  PlayAudioFile paf = new PlayAudioFile();
		  paf.init();
		  PlayAudioFile.songId = id;
		  Thread pafRunnable = new Thread(paf, "music");
		  pafRunnable.start();
          paf.promptWhenPlay();
	    } while(true);
	}
}

class MusicInfo {
	String musicName;

	public MusicInfo() {

	}
	public int getIndexOfMusic(boolean askForNameOfMusic, String nameOfMusic) {
      int indexOfAudioFile = 0;
      if(askForNameOfMusic == true) {
	     Scanner input = new Scanner(System.in);
         musicName = input.nextLine();
	  } else {
		 musicName = nameOfMusic;
	  }
      File audioFile = new File("./audio");
      File[] audioFiles = audioFile.listFiles();
      for(int i = 0; i < audioFiles.length; i++) {
		  if(musicName.equals(audioFiles[i].getName().replace(".mp3", ""))) {
            indexOfAudioFile = i;
		  }
	  }
	  return indexOfAudioFile;
	}
}

class LibraryMenu {
    public LibraryMenu() throws IOException, JavaLayerException, InterruptedException {
	    MusicDisplayer fileToReadFrom = new MusicDisplayer("./library.txt");
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        SelectMusic selectedMusic = new SelectMusic();
        selectedMusic.playSelected(num);
	}
}

class MusicDisplayer {
	public MusicDisplayer(String fileName) throws IOException, JavaLayerException, FileNotFoundException, InterruptedException {
	   PlayAudioFile paf = new PlayAudioFile();
	   paf.init();
	   FileReader musicNamesInFile = new FileReader(fileName);
	   char[] textToBeRead = new char[1000];
	   musicNamesInFile.read(textToBeRead, 0, 1000);
	   String textFromFile = "";
	   for(char eachChar: textToBeRead) {
		   textFromFile += eachChar;
	   }
	   String[] arrayOfFavorites = textFromFile.split("\n");
	   for(short i = 0; i < arrayOfFavorites.length; i++) {
		 System.out.println(arrayOfFavorites[i]);
	   }
       musicNamesInFile.close();
	}
}

class FavoriteMenu {
	public FavoriteMenu() throws IOException, JavaLayerException, InterruptedException {
		System.out.println("Choose a song from your favorite list to play.");
        System.out.println("Select a song to play by number: ");
        MusicDisplayer fileToReadFrom = new MusicDisplayer("./audio/favorite/fav.txt");
        Scanner scanner = new Scanner(System.in);
        int num = scanner.nextInt();
        SelectMusic selectedMusic = new SelectMusic();
        selectedMusic.playSelected(num);
	}
}

class FrontPage {
	public FrontPage() {

	}
	public void nextScreen() {
       try {
       new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
       } catch(IOException e) {
		   e.printStackTrace();
	   } catch (InterruptedException e) {
		   e.printStackTrace();
	   }
	}
	public void display() throws IOException, InterruptedException {
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
		    try {
		      musicTitle.searchSong();
		    } catch(JavaLayerException e) {
			  e.printStackTrace();
			}
		    break;
		case "l":
		    nextScreen();
		    try {
		      LibraryMenu libMenu = new LibraryMenu();
		    } catch(IOException e) {
				e.printStackTrace();
			} catch(JavaLayerException e) {
				e.printStackTrace();
			}
		    break;
		case "f":
		    nextScreen();
		    try {
		      FavoriteMenu favMenu = new FavoriteMenu();
		    } catch(IOException e) {
				e.printStackTrace();
			} catch(JavaLayerException e) {
				e.printStackTrace();
			}
		case "Q":
		case "q":
		    nextScreen();
			System.exit(0);
		    break;
	  }
	}
}

class SpotifyLike {
	public static void main(String[] args) {
		try {
          FrontPage fpg = new FrontPage();
          fpg.display();
	    } catch(IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
		    e.printStackTrace();
	    }
	}
}