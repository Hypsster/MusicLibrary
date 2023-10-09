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

    public Playlist createPlaylist(String filename) {
        StdIn.setFile(filename);

        // Beginning and the end of the playlists.
        SongNode first = null;
        SongNode last = null;
        // Counts the songs added to the playlists
        int songCount = 0;

        // IF there are more lines to read from, keep running.
        while (StdIn.hasNextLine()) {

            String line = StdIn.readLine();
            // Tries to make a song from the line
            Song newSong = parseSongFromLine(line);

            // If the top newSong fails, move to next line
            if (newSong == null) {
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
     * <p>
     * Adds a new playlist into the song library at a certain index.
     * <p>
     * 1. Calls createPlayList() with a file containing song information.
     * 2. Adds the new playlist created by createPlayList() into the songLibrary.
     * <p>
     * Note: initialize the songLibrary if it is null
     *
     * @param filename      the playlist information input file
     * @param playlistIndex the index of the location where the playlist will
     *                      be added
     */
    public void addPlaylist(String filename, int playlistIndex) {

        /* DO NOT UPDATE THIS METHOD */

        if (songLibrary == null) {
            songLibrary = new ArrayList<Playlist>();
        }
        if (playlistIndex >= songLibrary.size()) {
            songLibrary.add(createPlaylist(filename));
        } else {
            songLibrary.add(playlistIndex, createPlaylist(filename));
        }
    }

    /**
     * ****DO NOT**** UPDATE THIS METHOD
     * This method is already implemented for you.
     * <p>
     * It takes a playlistIndex, and removes the playlist located at that index.
     *
     * @param playlistIndex the index of the playlist to remove
     * @return true if the playlist has been deleted
     */
    public boolean removePlaylist(int playlistIndex) {
        /* DO NOT UPDATE THIS METHOD */

        if (songLibrary == null || playlistIndex >= songLibrary.size()) {
            return false;
        }

        songLibrary.remove(playlistIndex);

        return true;
    }

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

    public boolean removeSong(int playlistIndex, Song song) {
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
        if (playlistSize == 0) {
            return false; // Empty playlist, nothing to remove
        }

        SongNode current = playlist.getLast().getNext();  // Start at the first song.
        SongNode prev = playlist.getLast();  // Initialize prev to the last song.

        for (int i = 0; i < playlistSize; i++) {
            // Check if the current song is the one to remove.
            if (song.equals(current.getSong())) {
                // Special case: Removing the last song in the list.
                if (current == playlist.getLast()) {
                    playlist.setLast(prev);
                }

                // Remove the current node by adjusting the 'next' pointer of the previous node.
                prev.setNext(current.getNext());

                // Decrease the size of the playlist.
                playlist.setSize(playlistSize - 1);
                return true; // Song found and removed.
            }

            // Move to the next song.
            prev = current;
            current = current.getNext();
        }

        return false; // Song not found in the playlist.
    }

    public void reversePlaylist(int playlistIndex) {
        // Check if the playlistIndex is valid.
        if (playlistIndex < 0 || playlistIndex >= songLibrary.size()) {
            throw new IllegalArgumentException("Invalid playlist index.");
        }

        // Get the playlist and its last node.
        Playlist playlist = songLibrary.get(playlistIndex);
        SongNode lastNode = playlist.getLast();

        // Check if the playlist is empty or has only one song.
        if (lastNode == null || lastNode.getNext() == lastNode) {
            return; // Nothing to reverse.
        }

        // Initialize pointers.
        SongNode prev = lastNode;
        SongNode current = lastNode.getNext();
        SongNode next = null;
        SongNode firstNode = lastNode.getNext(); // Save the first node.

        do {
            next = current.getNext(); // Save the next node.
            current.setNext(prev);   // Reverse the link.
            prev = current;          // Move one step forward in the list.
            current = next;          // Move one step forward in the list.
        } while (current != firstNode); // Until we reach the starting node.

        // Update the last node in the playlist.
        playlist.setLast(firstNode);
    }

    //working
    public void mergePlaylists(int playlistIndex1, int playlistIndex2) {
        // [Step 1] Identify lower and higher index playlists
        int lowerIndex = Math.min(playlistIndex1, playlistIndex2);
        int higherIndex = Math.max(playlistIndex1, playlistIndex2);

        Playlist lowerPlaylist = songLibrary.get(lowerIndex);
        Playlist higherPlaylist = songLibrary.get(higherIndex);

        // [Step 2] Declare references for merged playlist
        SongNode mergedLast = null;
        SongNode mergedFirst = null;

        // [Step 3 & 4] While both playlists have songs, compare and move the higher popularity song
        while (lowerPlaylist.getSize() > 0 && higherPlaylist.getSize() > 0) {
            SongNode lowerFirst = lowerPlaylist.getLast().getNext();
            SongNode higherFirst = higherPlaylist.getLast().getNext();

            boolean useLower = lowerFirst.getSong().getPopularity() >= higherFirst.getSong().getPopularity();

            SongNode toMove;
            if (useLower) {
                toMove = lowerFirst;
                lowerPlaylist.getLast().setNext(lowerFirst.getNext());
                if (lowerFirst == lowerPlaylist.getLast()) {
                    lowerPlaylist.setLast(null);
                }
                lowerPlaylist.setSize(lowerPlaylist.getSize() - 1);
            } else {
                toMove = higherFirst;
                higherPlaylist.getLast().setNext(higherFirst.getNext());
                if (higherFirst == higherPlaylist.getLast()) {
                    higherPlaylist.setLast(null);
                }
                higherPlaylist.setSize(higherPlaylist.getSize() - 1);
            }

            if (mergedLast == null) {
                mergedLast = toMove;
                mergedFirst = toMove;
                toMove.setNext(toMove);
            } else {
                toMove.setNext(mergedFirst);
                mergedLast.setNext(toMove);
                mergedLast = toMove;
            }
        }

        // [Step 5] Append the non-empty playlist to the merged playlist
        Playlist nonEmptyPlaylist = lowerPlaylist.getSize() > 0 ? lowerPlaylist : higherPlaylist;
        if (mergedLast == null) {
            mergedLast = nonEmptyPlaylist.getLast();
            mergedFirst = nonEmptyPlaylist.getLast().getNext();
        } else if (nonEmptyPlaylist.getLast() != null) {
            mergedLast.setNext(nonEmptyPlaylist.getLast().getNext());
            nonEmptyPlaylist.getLast().setNext(mergedFirst);
        }

        // [Step 6] Assign the merged playlist to the lower index and remove the higher index playlist
        lowerPlaylist.setLast(mergedLast);
        lowerPlaylist.setSize(lowerPlaylist.getSize() + higherPlaylist.getSize());
        removePlaylist(higherIndex);

        // [Additional Step] Ensure the first song is the one with the highest popularity
        if (mergedLast != null) {
            SongNode current = mergedLast.getNext(); // Start from the first song
            SongNode highestPopularityNode = mergedLast;
            int highestPopularity = mergedLast.getSong().getPopularity();

            do {
                if (current.getSong().getPopularity() > highestPopularity) {
                    highestPopularity = current.getSong().getPopularity();
                    highestPopularityNode = current;
                }
                current = current.getNext();
            } while (current != mergedLast.getNext()); // Loop until we've checked all songs

            // Find the node that points to the highest popularity node
            SongNode pointerToHighestPopularityNode = mergedLast;
            while (pointerToHighestPopularityNode.getNext() != highestPopularityNode) {
                pointerToHighestPopularityNode = pointerToHighestPopularityNode.getNext();
            }

            // Set the last node in the playlist to be the one that points to the highest popularity song
            lowerPlaylist.setLast(pointerToHighestPopularityNode);
        }

    }



    public void shufflePlaylist(int playlistIndex) {
        // Validate playlist index
        if (playlistIndex < 0 || playlistIndex >= songLibrary.size()) {
            throw new IllegalArgumentException("Invalid playlist index.");
        }

        // Get the playlist to shuffle
        Playlist playlistToShuffle = songLibrary.get(playlistIndex);

        // Validate playlist
        if (playlistToShuffle == null || playlistToShuffle.getLast() == null) {
            throw new IllegalArgumentException("Invalid playlist.");
        }

        // Create a new playlist to store the shuffled playlist in
        Playlist shuffledPlaylist = new Playlist();

        // Get the size of the playlist to shuffle
        int size = playlistToShuffle.getSize();

        // While the size of the playlist is not 0
        while (size > 0) {
            // Randomly generate a number using StdRandom.uniformInt(size + 1)
            int randomIndex = StdRandom.uniformInt(size + 1);

            // Remove the corresponding node from the original playlist
            SongNode current = playlistToShuffle.getLast().getNext(); // Start at the first song
            SongNode prev = playlistToShuffle.getLast(); // Initialize prev to the last song
            SongNode toMove = null; // Node to move to the shuffled playlist

            // If removing the first song
            if (randomIndex == 1) {
                toMove = current;
                playlistToShuffle.getLast().setNext(current.getNext());
                if (current == playlistToShuffle.getLast()) { // If removing the last remaining song
                    playlistToShuffle.setLast(null);
                }
            } else {
                // Traverse to the song to remove
                for (int i = 1; i < randomIndex; i++) {
                    prev = current;
                    current = current.getNext();
                }
                toMove = current;
                prev.setNext(current.getNext());
                if (current == playlistToShuffle.getLast()) { // If removing the last song
                    playlistToShuffle.setLast(prev);
                }
            }

            // Insert the removed node into the END of the new playlist
            if (shuffledPlaylist.getLast() == null) {
                shuffledPlaylist.setLast(toMove);
                toMove.setNext(toMove);
            } else {
                toMove.setNext(shuffledPlaylist.getLast().getNext());
                shuffledPlaylist.getLast().setNext(toMove);
            }

            // Update the size
            size--;
        }

        // Update the old playlist with the new shuffled playlist
        songLibrary.set(playlistIndex, shuffledPlaylist);
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
