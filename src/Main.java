public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task_1", "Task_desc_1", Status.NEW);
        Task task2 = new Task("Task_2", "Task_desc_2", Status.IN_PROGRESS);
        Task task3 = new Task("Task_3", "Task_desc_3", Status.DONE);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        EpicTask epic1 = new EpicTask("Epic_1", "Epic_desc_1");
        EpicTask epic2 = new EpicTask("Epic_2", "Epic_desc_2");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subtask1 = new SubTask("Subtask_1_1", "Subtask_desc_1_1", Status.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Subtask_1_2", "Subtask_desc_1_2", Status.NEW, epic1.getId());
        SubTask subtask3 = new SubTask("Subtask_2_1", "Subtask_desc_2_1", Status.DONE, epic2.getId());
        SubTask subtask4 = new SubTask("Subtask_2_2", "Subtask_desc_2_2", Status.DONE, epic2.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        printAllTasks(taskManager);

        final Task task = taskManager.getTask(task1.getId());
        task.setStatus(Status.DONE);
        taskManager.updateTask(task1.getId(), task1);
        printAllTasks(taskManager);

        System.out.println("DELETE: " + task1.getName());
        taskManager.deleteTask(task1.getId());
        System.out.println("DELETE: " + epic1.getName());
        taskManager.deleteEpic(epic1.getId());
        printAllTasks(taskManager);

        System.out.println("\nУдалить все задачи:");
        taskManager.deleteAllTasks();
        printAllTasks(taskManager);

    }

    public static void printAllTasks(TaskManager taskManager) {
        System.out.println("\nTasks:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("\nEpics:");
        for (EpicTask e : taskManager.getEpics()) {
            System.out.println(e.toString());
            for (SubTask subTask : taskManager.getSubtasks(e.getId())) {
                System.out.println("--> " + subTask.toString());
            }
        }

        System.out.println("\nSubtasks:");
        for (SubTask subTask : taskManager.getSubtasks()) {
            System.out.println(subTask.toString());
        }
    }
}

