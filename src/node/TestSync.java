package node;

import java.util.List;

/**
 * This class triggers file synchronization from other nodes (e.g., Node 1)
 * into the local storage of this node (e.g., Node 2).
 */
public class TestSync {
    public static void main(String[] args) {
        // List of other nodes to sync from
        List<String> otherNodes = List.of("localhost");

        // Departments to sync
        List<String> departments = List.of("development", "design", "qa");

        // Start synchronization from other nodes
        SyncService sync = new SyncService("node_storage_node2", otherNodes);
        sync.syncAllDepartments(departments);
    }
}
