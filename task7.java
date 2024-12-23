// Базовый класс для всех животных
class Animal {
    public void makeSound() {
        System.out.println("This animal makes a sound.");
    }
}

// Подкласс: Собака
class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Woof! Woof!");
    }
}

// Подкласс: Кошка
class Cat extends Animal {
    @Override
    public void makeSound() {
        System.out.println("Meow! Meow!");
    }
}

// Пример работы
public class LiskovAnimalExample {
    public static void main(String[] args) {
        // Создаем массив животных
        Animal[] animals = { new Dog(), new Cat(), new Animal() };

        // Вызываем метод makeSound для каждого животного
        for (Animal animal : animals) {
            animal.makeSound();
        }
    }
}