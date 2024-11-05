package taskTracker;

import taskTracker.manager.InMemoryTaskManager;
import taskTracker.tasks.Epic;
import taskTracker.tasks.Status;
import taskTracker.tasks.SubTask;
import taskTracker.tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task("Task_1", "Task_desc_1", Status.NEW);
        Task task2 = new Task("Task_2", "Task_desc_2", Status.IN_PROGRESS);
        Task task3 = new Task("Task_3", "Task_desc_3", Status.DONE);

        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addTask(task3);

        Epic epic1 = new Epic("Epic_1", "Epic_desc_1");
        Epic epic2 = new Epic("Epic_2", "Epic_desc_2");
        Epic epic3 = new Epic("Epic_3", "Epic_desc_3");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);
        inMemoryTaskManager.addEpic(epic3);

        SubTask subtask1 = new SubTask("Subtask_1_1", "Subtask_desc_1_1", Status.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Subtask_1_2", "Subtask_desc_1_2", Status.DONE, epic1.getId());
        SubTask subtask3 = new SubTask("Subtask_2_1", "Subtask_desc_2_1", Status.DONE, epic2.getId());
        SubTask subtask4 = new SubTask("Subtask_2_2", "Subtask_desc_2_2", Status.DONE, epic2.getId());
        SubTask subtask5 = new SubTask("Subtask_3_1", "Subtask_desc_3_1", Status.NEW, epic3.getId());
        SubTask subtask6 = new SubTask("Subtask_3_2", "Subtask_desc_3_2", Status.NEW, epic3.getId());


        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.addSubtask(subtask3);
        inMemoryTaskManager.addSubtask(subtask4);
        inMemoryTaskManager.addSubtask(subtask5);
        inMemoryTaskManager.addSubtask(subtask6);
        printAllTasks(inMemoryTaskManager);

        final Task task = inMemoryTaskManager.getTaskById(task1.getId());
        task.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task1);

        printAllTasks(inMemoryTaskManager);

        System.out.println("DELETE: " + task1.getName());
        inMemoryTaskManager.deleteTask(task1.getId());
        System.out.println("DELETE: " + epic1.getName());
        inMemoryTaskManager.deleteEpic(epic1.getId());
        printAllTasks(inMemoryTaskManager);

        System.out.println("\nУдалить все задачи:");
        inMemoryTaskManager.deleteAllTasks();
        printAllTasks(inMemoryTaskManager);

    }

    public static void printAllTasks(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("\nTasks:");
        for (Task task : inMemoryTaskManager.getTasks()) {
            System.out.println(task.toString());
        }

        System.out.println("\nEpics:");
        for (Epic epic : inMemoryTaskManager.getEpics()) {

            System.out.println("\n" + epic.toString());

            for (SubTask subTask : inMemoryTaskManager.getSubtasksByEpicId(epic.getId())) {

                System.out.println("--> " + subTask.toString());
            }
        }

        System.out.println("\nSubtasks:");
        for (SubTask subTask : inMemoryTaskManager.getSubtasks()) {
            System.out.println(subTask.toString());
        }
    }
}

