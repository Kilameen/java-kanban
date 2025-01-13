package tracker;

import tracker.manager.Managers;
import tracker.manager.TaskManager;
import tracker.tasks.Epic;
import tracker.tasks.Status;
import tracker.tasks.SubTask;
import tracker.tasks.Task;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        //Создаем 2 задачи

        Task task1 = new Task("Task_1", Status.NEW, "Task_desc_1", LocalDateTime.now(), 15L);
        Task task2 = new Task("Task_2", Status.IN_PROGRESS, "Task_desc_2", task1.getEndTime().plusHours(2), 15L);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        //Создаем 2 эпика, один эпик с 3 подзадачами, один пустой

        Epic epic1 = new Epic("Epic_1", Status.NEW, "Epic_desc_1", task2.getEndTime().plusHours(1), 15L);
        Epic epic2 = new Epic("Epic_2", Status.DONE, "Epic_desc_2", task2.getEndTime().plusMinutes(45), 15L);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());

        SubTask subtask1 = new SubTask("Subtask_1_1", Status.NEW, "Subtask_desc_1_1", task2.getEndTime().plusHours(1), 15L, epic1.getId());
        SubTask subtask2 = new SubTask("Subtask_1_2", Status.IN_PROGRESS, "Subtask_desc_1_2", subtask1.getEndTime().plusHours(1), 15L, epic1.getId());
        SubTask subtask3 = new SubTask("Subtask_1_3", Status.DONE, "Subtask_desc_1_3", subtask2.getEndTime().plusHours(2), 15L, epic1.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());

        System.out.println("Показать приоритетные задачи : ");
        System.out.println(taskManager.getPrioritizedTasks());
        //Запрашиваем созданные задачи несколько раз в разном порядке.

        System.out.println("\n Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(1));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(2));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(1));

        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        System.out.println("\n Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(2));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(1));
        System.out.println("Запрос созданной задачи : \n" + "\n" + taskManager.getTaskById(2));

        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        //Удаляем задчу и проверям историю
        System.out.println("Удаляем задачу: " + task1.getName());
        taskManager.deleteTask(task1.getId());
        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        //Удаляем эпик и проверяем историю
        System.out.println("Удаляем эпик: " + epic1.getName());
        taskManager.deleteEpic(epic1.getId());
        System.out.println("\n Показать историю : \n" + taskManager.getHistory());

        System.out.println("_".repeat(100));
        System.out.println("Выводы в консоль 5-го спринта");
        printAllTasks(taskManager);

        final Task task = taskManager.getTaskById(task2.getId());
        task.setStatus(Status.DONE);
        taskManager.updateTask(task2);

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
        for (Epic epic : taskManager.getEpics()) {
            System.out.println("\n" + epic.toString());
            for (SubTask subTask : taskManager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + subTask.toString());
            }
        }

        System.out.println("\nSubtasks:");
        for (SubTask subTask : taskManager.getSubtasks()) {
            System.out.println(subTask.toString());
        }

        System.out.println("\nHistory:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}