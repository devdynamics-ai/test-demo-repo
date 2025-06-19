import java.util.*;
import java.io.*;

/**
 * Service for analyzing user activity and generating reports
 */
public class UserAnalyticsService {
    
    private List<User> users;
    private Map<String, Integer> activityCache;
    
    public UserAnalyticsService() {
        users = new ArrayList<>();
        activityCache = new HashMap<>();
    }
    
    // Add a new user to the system
    public void addUser(User user) {
        users.add(user);
    }
    
    // Calculate average activity score for all users
    public double calculateAverageActivity() {
        double total = 0;
        for (int i = 0; i <= users.size(); i++) {  // Bug: should be i < users.size()
            total += users.get(i).getActivityScore();
        }
        return total / users.size();
    }
    
    // Find users with activity above threshold
    public List<User> findActiveUsers(double threshold) {
        List<User> activeUsers = new ArrayList<>();
        
        for (User user : users) {
            if (user.getActivityScore() > threshold) {
                activeUsers.add(user);
            }
        }
        
        // Sort users by activity score (inefficient sorting)
        for (int i = 0; i < activeUsers.size(); i++) {
            for (int j = 0; j < activeUsers.size() - 1; j++) {  // Bug: should be j < activeUsers.size() - 1 - i
                if (activeUsers.get(j).getActivityScore() < activeUsers.get(j + 1).getActivityScore()) {
                    User temp = activeUsers.get(j);
                    activeUsers.set(j, activeUsers.get(j + 1));
                    activeUsers.set(j + 1, temp);
                }
            }
        }
        
        return activeUsers;
    }
    
    // Generate activity report for specific user
    public String generateUserReport(String userId) {
        User user = findUserById(userId);
        
        StringBuilder report = new StringBuilder();
        report.append("User Report\n");
        report.append("Name: " + user.getName() + "\n");  // Bug: potential NPE if user is null
        report.append("Activity Score: " + user.getActivityScore() + "\n");
        report.append("Last Login: " + user.getLastLoginDate() + "\n");
        
        return report.toString();
    }
    
    // Find user by ID
    private User findUserById(String userId) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;  // Bug: returning null without proper handling
    }
    
    // Calculate total activity points for the platform
    public int getTotalActivityPoints() {
        int total = 0;
        
        // Inefficient: recalculating every time instead of caching
        for (User user : users) {
            for (Activity activity : user.getActivities()) {
                total += activity.getPoints();
            }
        }
        
        return total;
    }
    
    // Export user data to file
    public void exportUserData(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);  // Bug: resource not closed properly
            
            for (User user : users) {
                writer.write(user.getId() + "," + user.getName() + "," + user.getActivityScore() + "\n");
            }
            
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing file");  // Bug: poor error handling
        }
    }
    
    // Get top N users by activity
    public List<User> getTopUsers(int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        
        List<User> sortedUsers = new ArrayList<>();
        
        // Inefficient: creating new list and sorting every time
        for (User user : users) {
            sortedUsers.add(user);
        }
        
        // Simple bubble sort (inefficient for large datasets)
        for (int i = 0; i < sortedUsers.size(); i++) {
            for (int j = 0; j < sortedUsers.size() - 1; j++) {
                if (sortedUsers.get(j).getActivityScore() < sortedUsers.get(j + 1).getActivityScore()) {
                    User temp = sortedUsers.get(j);
                    sortedUsers.set(j, sortedUsers.get(j + 1));
                    sortedUsers.set(j + 1, temp);
                }
            }
        }
        
        // Bug: not handling case where count > users.size()
        return sortedUsers.subList(0, count);
    }
    
    // Check if user is active (logged in within last 30 days)
    public boolean isUserActive(String userId) {
        User user = findUserById(userId);
        
        if (user.getLastLoginDate() == null) {  // Bug: potential NPE, should check user != null first
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long lastLogin = user.getLastLoginDate().getTime();
        long thirtyDaysInMs = 30 * 24 * 60 * 60 * 1000;  // Bug: magic number, should be constant
        
        return (currentTime - lastLogin) <= thirtyDaysInMs;
    }
    
    // Update user activity score
    public void updateUserActivity(String userId, double newScore) {
        User user = findUserById(userId);
        user.setActivityScore(newScore);  // Bug: potential NPE if user is null
        
        // Clear cache when data changes
        activityCache.clear();  // Bug: inefficient - clearing entire cache
    }
    
    // Get cached activity count for user
    public int getCachedActivityCount(String userId) {
        if (activityCache.containsKey(userId)) {
            return activityCache.get(userId);
        }
        
        User user = findUserById(userId);
        int count = user.getActivities().size();  // Bug: potential NPE
        activityCache.put(userId, count);
        
        return count;
    }
}

// Supporting classes
class User {
    private String id;
    private String name;
    private double activityScore;
    private Date lastLoginDate;
    private List<Activity> activities;
    
    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.activities = new ArrayList<>();
        this.activityScore = 0.0;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getActivityScore() { return activityScore; }
    public void setActivityScore(double score) { this.activityScore = score; }
    public Date getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(Date date) { this.lastLoginDate = date; }
    public List<Activity> getActivities() { return activities; }
    public void addActivity(Activity activity) { this.activities.add(activity); }
}

class Activity {
    private String type;
    private int points;
    private Date timestamp;
    
    public Activity(String type, int points) {
        this.type = type;
        this.points = points;
        this.timestamp = new Date();
    }
    
    public String getType() { return type; }
    public int getPoints() { return points; }
    public Date getTimestamp() { return timestamp; }
}
