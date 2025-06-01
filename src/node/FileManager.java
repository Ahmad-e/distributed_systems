package node;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileManager {
    private String basePath;
    private ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    public FileManager(String basePath) {
        this.basePath = basePath;
    }

    private ReentrantReadWriteLock getLock(String department, String filename) {
        String key = department + "/" + filename;
        locks.putIfAbsent(key, new ReentrantReadWriteLock(true));
        return locks.get(key);
    }

    public String saveFile(String department, String filename, String content) {
        ReentrantReadWriteLock lock = getLock(department, filename);
        lock.writeLock().lock();
        try {
            Path path = Paths.get(basePath, department, filename);
            Files.write(path, content.getBytes(), StandardOpenOption.CREATE_NEW);
            return "File uploaded successfully.";
        } catch (FileAlreadyExistsException e) {
            return "Error: File already exists.";
        } catch (IOException e) {
            return "Error saving file: " + e.getMessage();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String updateFile(String department, String filename, String content) {
        ReentrantReadWriteLock lock = getLock(department, filename);
        lock.writeLock().lock();
        try {
            Path path = Paths.get(basePath, department, filename);
            if (!Files.exists(path)) return "Error: File does not exist.";
            Files.write(path, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return "File updated successfully.";
        } catch (IOException e) {
            return "Error updating file: " + e.getMessage();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String deleteFile(String department, String filename) {
        ReentrantReadWriteLock lock = getLock(department, filename);
        lock.writeLock().lock();
        try {
            Path path = Paths.get(basePath, department, filename);
            if (!Files.exists(path)) return "Error: File not found.";
            Files.delete(path);
            return "File deleted successfully.";
        } catch (IOException e) {
            return "Error deleting file: " + e.getMessage();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String readFile(String department, String filename) {
        ReentrantReadWriteLock lock = getLock(department, filename);
        lock.readLock().lock();
        try {
            Path path = Paths.get(basePath, department, filename);
            if (!Files.exists(path)) return "Error: File not found.";
            return Files.readString(path);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        } finally {
            lock.readLock().unlock();
        }
    }
}
