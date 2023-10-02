package music;

import java.util.*;

public class PlaylistLibrary {

    private ArrayList<Playlist> songLibrary; // contains various playlists

    public PlaylistLibrary(ArrayList<Playlist> songLibrary) {
        this.songLibrary = songLibrary;
    }

    public PlaylistLibrary() {
        this(null);
    }

    public Playlist createPlaylist(String filename)
    {
        StdIn.setFile(filename);

        // Beginning and the end of the playlists.
        SongNode first = null;
        SongNode last = null;
        // Counts the songs added to the playlists
        int songCount = 0;

        // IF there are more lines to read from, keep running.
        while(StdIn.hasNextLine())
        {

            String line = StdIn.readLine();
            // Tries to make a song from the line
            Song newSong = parseSongFromLine(line);

            // If the top newSong fails, move to next line
            if (newSong == null)
            {
                continue;
            }

            // Create the container for the song.
            SongNode newNode = new SongNode(newSong, null);

            // if this is the first song added
            if (first == null) {
                first = newNode; // declare start
                last = newNode; // declare end
                last.setNext(first);
            } else {
                // if this isn't the first song
                newNode.setNext(first);
                last.setNext(newNode);
                last = newNode;
            }
            songCount++;
        }

        return new Playlist(last, songCount); // return the play list
    }
    // Create a song from a line, adds song details,
    private Song parseSongFromLine(String line) {
        String[] songDetails = line.split(",");

        // if the song doesn't contain all details, then the song is incomplete and wont make the song
        if (songDetails.length != 5) {
            return null;
        }

        // attempt to create a song
        try {
            return new Song(songDetails[0], songDetails[1], Integer.parseInt(songDetails[2]), Integer.parseInt(songDetails[3]), songDetails[4]);
        } catch (NumberFormatException e) {
            // if the song details are invalid, "Year is not a # or something"
            return null;
        }
    }

    /**
     * ****DO NOT**** UPDATE THIS METHOD
     * This method is already implemented for you. 
     * 
     * Adds a new playlist into the song library at a certain index.
     * 
     * 1. Calls createPlayList() with a file containing song information.
     * 2. Adds the new playlist created by createPlayList() into the songLibrary.
     * 
     * Note: initialize the songLibrary if it is null
     * 
     * @param filename the playlist information input file
     * @param playlistIndex the index of the location where the playlist will 
     * be added 
     */
    public void addPlaylist(String filename, int playlistIndex) {
        
        /* DO NOT UPDATE THIS METHOD */

        if ( songLibrary == null ) {
            songLibrary = new ArrayList<Playlist>();
        }
        if ( playlistIndex >= songLibrary.size() ) {
            songLibrary.add(createPlaylist(filename));
        } else {
            songLibrary.add(playlistIndex, createPlaylist(filename));
        }        
    }
    /**
     * ****DO NOT**** UPDATE THIS METHOD
     * This method is already implemented for you.
     * 
     * It takes a playlistIndex, and removes the playlist located at that index.
     * 
     * @param playlistIndex the index of the playlist to remove
     * @return true if the playlist has been deleted
     */
    public boolean removePlaylist(int playlistIndex) {
        /* DO NOT UPDATE THIS METHOD */

        if ( songLibrary == null || playlistIndex >= songLibrary.size() ) {
            return false;
        }

        songLibrary.remove(playlistIndex);
            
        return true;
    }
    
    /** 
     * 
     * Adds the playlists from many files into the songLibrary
     * 
     * 1. Initialize the songLibrary
     * 
     * 2. For each of the filenames
     *       add the playlist into songLibrary
     * 
     * The playlist will have the same index in songLibrary as it has in
     * the filenames array. For example if the playlist is being created
     * from the filename[i] it will be added to songLibrary[i]. 
     * Use the addPlaylist() method. 
     * 
     * @param filenames an array of the filenames of playlists that should be 
     * added to the library
     */
    public void addAllPlaylists(String[] filenames) {

        // 1. Initialize the songLibrary if it is null.
        // Check if songLibrary is null. If it is, create a new ArrayList for it.
        if (songLibrary == null) {
            songLibrary = new ArrayList<Playlist>();
        }

        // 2. For each filename in our filenames array...
        for (String filename : filenames) {
            // Using the provided addPlaylist method,
            // add a playlist for each filename to the end of the songLibrary.
            addPlaylist(filename, songLibrary.size());  // The size of the songLibrary gives us the next available index.
        }
    }

    /**
     * This method adds a song to a specified playlist at a given position.
     * 
     * The first node of the circular linked list is at position 1, the 
     * second node is at position 2 and so forth.
     * 
     * Return true if the song can be added at the given position within the 
     * specified playlist (and thus has been added to the playlist), false 
     * otherwise (and the song will not be added). 
     * 
     * Increment the size of the playlist if the song has been successfully
     * added to the playlist.
     * 
     * @param playlistIndex the index where the playlist will be added
     * @param position the position inthe playlist to which the song 
     * is to be added 
     * @param song the song to add
     * @return true if the song can be added and therefore has been added, 
     * false otherwise. 
     */
    public boolean insertSong(int playlistIndex, int position, Song song) {

        // Ensure songLibrary has been initialized
        if (songLibrary == null) {
            return false;
        }

        // Step 1: Check if the playlist index is valid.
        if (playlistIndex < 0 || playlistIndex >= songLibrary.size()) {
            return false;
        }

        Playlist playlist = songLibrary.get(playlistIndex);


        // Check if the playlist or its last node is null
        if (playlist == null || playlist.getLast() == null) {
            return false;
        }

        int playlistSize = playlist.getSize();

        // Step 2: Check if the position is valid.
        if (position < 1 || position > playlistSize + 1) {
            return false;
        }

        // Create the new song node.
        SongNode newNode = new SongNode(song, null);

        // Step 3: Insert the song at the given position.

        // Special case: inserting at the beginning.
        if (position == 1) {
            newNode.setNext(playlist.getLast().getNext()); // Point to the current first song.
            playlist.getLast().setNext(newNode);  // Update the last song's next pointer.
            if (playlistSize == 0) { // If the playlist was empty, this is also the last song.
                playlist.setLast(newNode);
            }
        } else {
            SongNode current = playlist.getLast().getNext(); // Start at the first song.
            for (int i = 1; i < position - 1; i++) { // Traverse until the position before where we want to insert.
                current = current.getNext();

                // Ensure current's next is not null before moving on
                if (current == null || current.getNext() == null) {
                    return false;
                }
        }
            newNode.setNext(current.getNext());  // Set the new node's next pointer.
            current.setNext(newNode);  // Insert the new node after the current node.

            // If we're inserting at the end, update the playlist's last node pointer.
            if (position == playlistSize + 1) {
                playlist.setLast(newNode);
            }
        }
        // Step 4: Increase the playlist's size.
        playlist.setSize(playlistSize + 1);

        return true;
    }

    /**
     * This method removes a song at a specified playlist, if the song exists. 
     *
     * Use the .equals() method of the Song class to check if an element of 
     * the circular linkedlist matches the specified song.
     * 
     * Return true if the song is found in the playlist (and thus has been 
     * removed), false otherwise (and thus nothing is removed). 
     * 
     * Decrease the playlist size by one if the song has been successfully
     * removed from the playlist.
     * 
     * @param playlistIndex the playlist index within the songLibrary where 
     * the song is to be added.
     * @param song the song to remove.
     * @return true if the song is present in the playlist and therefore has 
     * been removed, false otherwise.
     */
    public boolean removeSong(int playlistIndex, Song song) {
        // WRITE YOUR CODE HERE

        return false; // update the return value
    }

    /**
     * This method reverses the playlist located at playlistIndex
     * 
     * Each node in the circular linked list will point to the element that 
     * came before it.
     * 
     * After the list is reversed, the playlist located at playlistIndex will 
     * reference the first SongNode in the original playlist (new last).
     * 
     * @param playlistIndex the playlist to reverse
     */
    public void reversePlaylist(int playlistIndex) {
        // WRITE YOUR CODE HERE
    }

    /**
     * This method merges two playlists.
     * 
     * Both playlists have songs in decreasing popularity order. The resulting 
     * playlist will also be in decreasing popularity order.
     * 
     * You may assume both playlists are already in decreasing popularity 
     * order. If the songs have the same popularity, add the song from the 
     * playlist with the lower playlistIndex first.
     * 
     * After the lists have been merged:
     *  - store the merged playlist at the lower playlistIndex
     *  - remove playlist at the higher playlistIndex 
     * 
     * 
     * @param playlistIndex1 the first playlist to merge into one playlist
     * @param playlistIndex2 the second playlist to merge into one playlist
     */
    public void mergePlaylists(int playlistIndex1, int playlistIndex2) {
      
        // WRITE YOUR CODE HERE
    }

    /**
     * This method shuffles a specified playlist using the following procedure:
     * 
     * 1. Create a new playlist to store the shuffled playlist in.
     * 
     * 2. While the size of the original playlist is not 0, randomly generate a number 
     * using StdRandom.uniformInt(1, size+1). Size contains the current number
     * of items in the original playlist.
     * 
     * 3. Remove the corresponding node from the original playlist and insert 
     * it into the END of the new playlist (1 being the first node, 2 being the 
     * second, etc). 
     * 
     * 4. Update the old playlist with the new shuffled playlist.
     *
     */
    public void shufflePlaylist(int playlistIndex) {
        // WRITE YOUR CODE HERE

    }

    /**
     * This method sorts a specified playlist using linearithmic sort.
     * 
     * Set the playlist located at the corresponding playlistIndex
     * in decreasing popularity index order.
     * 
     * This method should  use a sort that has O(nlogn), such as with merge sort.
     * 
     * @param playlistIndex the playlist to shuffle
     */
    public void sortPlaylist ( int playlistIndex ) {

        // WRITE YOUR CODE HERE
        
    }

    /**
     * ****DO NOT**** UPDATE THIS METHOD
     * Plays playlist by index; can use this method to debug.
     * 
     * @param playlistIndex the playlist to print
     * @param repeats number of times to repeat playlist
     * @throws InterruptedException
     */
    public void playPlaylist(int playlistIndex, int repeats) {
        /* DO NOT UPDATE THIS METHOD */

        final String NO_SONG_MSG = " has no link to a song! Playing next...";
        if (songLibrary.get(playlistIndex).getLast() == null) {
            StdOut.println("Nothing to play.");
            return;
        }

        SongNode ptr = songLibrary.get(playlistIndex).getLast().getNext(), first = ptr;

        do {
            StdOut.print("\r" + ptr.getSong().toString());
            if (ptr.getSong().getLink() != null) {
                StdAudio.play(ptr.getSong().getLink());
                for (int ii = 0; ii < ptr.getSong().toString().length(); ii++)
                    StdOut.print("\b \b");
            }
            else {
                StdOut.print(NO_SONG_MSG);
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
                for (int ii = 0; ii < NO_SONG_MSG.length(); ii++)
                    StdOut.print("\b \b");
            }

            ptr = ptr.getNext();
            if (ptr == first) repeats--;
        } while (ptr != first || repeats > 0);
    }

    /**
     * ****DO NOT**** UPDATE THIS METHOD
     * Prints playlist by index; can use this method to debug.
     * 
     * @param playlistIndex the playlist to print
     */
    public void printPlaylist(int playlistIndex) {
        StdOut.printf("%nPlaylist at index %d (%d song(s)):%n", playlistIndex, songLibrary.get(playlistIndex).getSize());
        if (songLibrary.get(playlistIndex).getLast() == null) {
            StdOut.println("EMPTY");
            return;
        }
        SongNode ptr;
        for (ptr = songLibrary.get(playlistIndex).getLast().getNext(); ptr != songLibrary.get(playlistIndex).getLast(); ptr = ptr.getNext() ) {
            StdOut.print(ptr.getSong().toString() + " -> ");
        }
        if (ptr == songLibrary.get(playlistIndex).getLast()) {
            StdOut.print(songLibrary.get(playlistIndex).getLast().getSong().toString() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    public void printLibrary() {
        if (songLibrary.size() == 0) {
            StdOut.println("\nYour library is empty!");
        } else {
                for (int ii = 0; ii < songLibrary.size(); ii++) {
                printPlaylist(ii);
            }
        }
    }

    /*
     * Used to get and set objects.
     * DO NOT edit.
     */
     public ArrayList<Playlist> getPlaylists() { return songLibrary; }
     public void setPlaylists(ArrayList<Playlist> p) { songLibrary = p; }
}
