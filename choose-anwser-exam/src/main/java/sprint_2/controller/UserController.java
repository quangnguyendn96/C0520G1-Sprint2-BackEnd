package sprint_2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sprint_2.dto.ChangePasswordDTO;
import sprint_2.dto.ImageDTO;
import sprint_2.dto.UserManagerDTO;
import sprint_2.model.Question;
import sprint_2.model.ResultExam;
import sprint_2.model.User;
import sprint_2.service.ResultExamService;
import sprint_2.service.RoleService;
import sprint_2.service.UserService;
import sprint_2.dto.ExamHistoryDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * UserController
 * <p>
 * Version 1.0
 * <p>
 * Date: 08-12-2020
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE                 AUTHOR          DESCRIPTION
 * -----------------------------------------------------------------------
 * 08-12-2020         NhatL/Tra           CRUD
 */

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;
    @Autowired
    ResultExamService resultExamService;
    @Autowired
    PasswordEncoder passwordEncoder;

     /**
     * get data for User list page
     *
     * @param
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<UserManagerDTO>> getListUser() {
        List<User> userList = userService.findAll();
        List<UserManagerDTO> userListDTO = new ArrayList<>();
        double point = 0;
        if (userList == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            for (User user : userList) {
                if (resultExamService.findUserByIdPointTime(user.getIdUser()).size() == 0) {
                    userListDTO.add(new UserManagerDTO(user.getIdUser(), user.getUsername(), user.getPassword(), user.getFullName(), user.getEmail(), user.getAddress(), user.getPhoneNumber(), user.getImage(), "0", "0"));
                } else {
                    for (ResultExam resultExam : resultExamService.findUserByIdPointTime(user.getIdUser()))
                        point += Double.parseDouble(resultExam.getMark());
                    //phuong thuc size dung de lay times khong the sua
                    userListDTO.add(new UserManagerDTO(user.getIdUser(), user.getUsername(), user.getPassword(), user.getFullName(), user.getEmail(), user.getAddress(), user.getPhoneNumber(), user.getImage(), String.valueOf(point), String.valueOf(resultExamService.findUserByIdPointTime(user.getIdUser()).size())));
                }
                point = 0;
            }
            return new ResponseEntity<>(userListDTO, HttpStatus.OK);
        }
    }

    /**
     * get data for User list page
     *
     * @param idUser
     * @return
     */
    @GetMapping("/{idUser}")
    public ResponseEntity<User> getUser(@PathVariable Long idUser) {
        User user = userService.findById(idUser);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * create user
     *
     * @param
     * @return
     */
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createUser(@Validated({User.checkCreate.class, User.checkEdit.class})
                                           @RequestBody User user, BindingResult bindingResult) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                user.setRole(roleService.findById((long) 2));
                user.setImage("");
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.save(user);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
    }

    /**
     * edit user
     *
     * @param idUser,user
     * @return
     */
    @PutMapping("/edit/{idUser}")
    public ResponseEntity<Void> editUser(@PathVariable Long idUser, @RequestBody User user) {
        User userNew = userService.findById(idUser);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            userNew.setFullName(user.getFullName());
            userNew.setEmail(user.getEmail());
            userNew.setAddress(user.getAddress());
            userNew.setPhoneNumber(user.getPhoneNumber());
            userService.save(userNew);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * delete asset by idUser
     *
     * @param idUser
     * @return
     */
    @DeleteMapping("/delete/{idUser}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long idUser) {
        User user = userService.findById(idUser);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteById(idUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * get data for user variable
     *
     * @param id
     * @return user
     */
    @GetMapping("/findById/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") long id) {
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * get data for user1 variable, update information then save it
     *
     * @param user, id
     * @return message
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateAccount(@RequestBody User user, @PathVariable Long id) {
        User user1 = userService.findById(id);
        if (user1 == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        user1.setFullName(user.getFullName());
        user1.setEmail(user.getEmail());
        user1.setAddress(user.getAddress());
        user1.setPhoneNumber(user.getPhoneNumber());
        userService.save(user1);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * change password user
     *
     * @param changePasswordDTO, id
     * @return errorsList
     */
    @PutMapping(value = "/{id}/change-password")
    public ResponseEntity<?> changePassWordUser(@Validated @RequestBody ChangePasswordDTO changePasswordDTO,
                                                @PathVariable("id") long id) {
        User user = userService.findById(id);
        List<ChangePasswordDTO> errorsList = new ArrayList<>();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        if (BCrypt.checkpw(changePasswordDTO.getOldPassword(), user.getPassword())) {
            userService.changePassWord(id, passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            errorsList.add(new ChangePasswordDTO("Mật khẩu không chính xác"));
            return new ResponseEntity<>(errorsList, HttpStatus.OK);
        }
    }

    /**
     * find all history of exam
     *
     * @param id
     * @return examHistoryDTOList, message
     */
    @GetMapping("/findExamHistoryById/{id}")
    public ResponseEntity<List<ExamHistoryDTO>> getExamHistory(@PathVariable Long id) {
        List<ExamHistoryDTO> examHistoryDTOList = new ArrayList<>();
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            Set<Question> questionSet;
            List<Question> questionList;
            for (ResultExam resultExam : user.getResultExamCollection()) {
                questionList = new ArrayList<>();
                questionSet = resultExam.getExam().getQuestions();
                questionList.addAll(questionSet);
                examHistoryDTOList.add(new ExamHistoryDTO(
                        questionList.get(0).getSubject().getSubjectName(),
                        resultExam.getExam().getExamName(),
                        resultExam.getMark(),
                        resultExam.getTakenDate()));
            }
        }
        return new ResponseEntity<>(examHistoryDTOList, HttpStatus.OK);
    }

    @PutMapping("/{id}/change-image")
    public ResponseEntity<Void> updateImage(@PathVariable long id, @RequestBody ImageDTO imageDTO){
        User user = userService.findById(id);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            user.setImage(imageDTO.getImage());
            userService.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }
}
