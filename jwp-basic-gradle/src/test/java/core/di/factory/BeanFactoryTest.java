package core.di.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import core.di.factory.example.MyQnaService;
import core.di.factory.example.MyUserController;
import core.di.factory.example.MyUserService;
import core.di.factory.example.QnaController;
import core.di.factory.example.PriorityUserRepository;
import core.di.factory.example.UserRepository;
import core.di.factory.inject.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

public class BeanFactoryTest {
    private Reflections reflections;
    private ApplicationContext applicationContext;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        applicationContext = new ApplicationContext("core.di.factory.example");
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = applicationContext.getBean(QnaController.class);
        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @Test
    public void fieldDi() throws Exception {
        MyUserService myUserService = applicationContext.getBean(MyUserService.class);
        assertNotNull(myUserService);
        assertNotNull(myUserService.getUserRepository());
    }

    @Test
    public void setterDi() throws Exception {
        MyUserController myUserController = applicationContext.getBean(MyUserController.class);
        assertNotNull(myUserController);
        assertNotNull(myUserController.getUserService());
    }

    @Test
    public void InterfacePriorityDITest() throws Exception {
        UserRepository Repository = applicationContext.getBean(PriorityUserRepository.class);
        assertNotNull(Repository);
    }
}
