package ru.kudinov.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kudinov.model.*;
import ru.kudinov.model.enums.entityEnums.*;
import ru.kudinov.model.enums.searchEnums.UserSearchType;
import ru.kudinov.model.enums.sortEnums.UserSortType;
import ru.kudinov.service.*;
import ru.kudinov.util.ImageUtil;
import ru.kudinov.util.PaginationUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final DetailService detailService;
    private final RequestService requestService;
    private final ServService servService;
    private final ServOrganizationService servOrganizationService;
    private final EmployeeService employeeService;
    private final ImageUtil imageUtil;
    private final PaginationUtil paginationUtil;

    @Value("${detail.file-directory}")
    private String detailFileDirectory;

    @Value("${employee.file-directory}")
    private String employeeFileDirectory;

    @Value("${default.employee-image.path}")
    private String defaultEmployeeImagePath;

    @Value("${default.user-image.path}")
    private String defaultUserImagePath;

    public AdminController(UserService userService, DetailService detailService, RequestService requestService,
                           ServService servService, ServOrganizationService servOrganizationService,
                           EmployeeService employeeService, ImageUtil imageUtil, PaginationUtil paginationUtil) {
        this.userService = userService;
        this.detailService = detailService;
        this.requestService = requestService;
        this.servService = servService;
        this.servOrganizationService = servOrganizationService;
        this.employeeService = employeeService;
        this.imageUtil = imageUtil;
        this.paginationUtil = paginationUtil;
    }

    @GetMapping("getUsers")
    public String getUserListPage(Model model, @AuthenticationPrincipal User user,
                                  @RequestParam(name = "page", defaultValue = "0") String pageNumber,
                                  @RequestParam(defaultValue = "0", required = false) String userSearchTypeOrd,
                                  @RequestParam(required = false) String userSearchInfo,
                                  HttpServletRequest request) {

        UserSortType userSortType = UserSortType.ID_DESC;

        Pageable pageable = paginationUtil.createPageable(pageNumber, userSortType);

        Page<User> userPage;

        if (userSearchInfo == null || userSearchInfo.equals("")) userPage = userService.allUsers(pageable);
        else {
            User userExample = new User();

            switch (UserSearchType.values()[Integer.parseInt(userSearchTypeOrd)]) {
                case ID -> userExample.setId(Long.parseLong(userSearchInfo));
                case USERNAME -> userExample.setUsername(userSearchInfo);
                case NAME -> userExample.setName(userSearchInfo);
                case SURNAME -> userExample.setSurname(userSearchInfo);
                case PATRONYMIC -> userExample.setPatronymic(userSearchInfo);
                case SNP -> {
                    String[] userInfo = userSearchInfo.split(" ");
                    userExample.setSurname(userInfo[0]);
                    userExample.setName(userInfo[1]);
                    userExample.setPatronymic(userInfo[2]);
                }
                case EMAIL -> userExample.setEmail(userSearchInfo);
                case PHONE_NUMBER -> userExample.setPhoneNumber(userSearchInfo);
            }

            ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase();
            Example<User> example = Example.of(userExample, matcher);

            userPage = userService.findUsersByExample(example, pageable);
        }

        paginationUtil.addPaginationInModel(model, "userPage", userPage, request);

        model.addAttribute("user", user);
        model.addAttribute("userSearchTypes", UserSearchType.values());
        model.addAttribute("currentUserSearchType", UserSearchType.values()[Integer.parseInt(userSearchTypeOrd)]);
        model.addAttribute("currentUserSearchInfo", userSearchInfo);

        return "getUsers";
    }

    @GetMapping("deleteUser/{user}")
    public String deleteUser(RedirectAttributes redirectAttributes, @PathVariable User user) throws IOException {

        if (!Objects.equals(user.getImage(), defaultUserImagePath)) imageUtil.deleteFile(user.getImage());
        userService.deleteUser(user);
        redirectAttributes.addFlashAttribute("message", "Пользователь успешно удален");

        return "redirect:/admin/getUsers";
    }

    @GetMapping("getRequests")
    public String getRequestsPage(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        model.addAttribute("requests", requestService.allRequests(RequestStatus.CONFIRMED));
        return "getRequests";
    }

    @GetMapping("getDetails")
    public String getDetailsPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("details", detailService.allDetails());
        model.addAttribute("user", user);
        return "/getDetails";
    }

    @GetMapping("editDetail/{detail}")
    public String getEditDetailPage(Model model, @PathVariable Detail detail, @AuthenticationPrincipal User user) {
        model.addAttribute("detail", detail);
        model.addAttribute("detailTypes", DetailType.values());
        model.addAttribute("user", user);

        return "editDetail";
    }

    @PostMapping("editDetail/{detail}")
    public String editDetail(Model model, @AuthenticationPrincipal User user,
                             @ModelAttribute Detail editDetail,
                             @RequestParam int selectDetailType,
                             @RequestParam String oldDetailName,
                             @RequestParam(required = false) MultipartFile[] uploadImages,
                             @RequestParam(required = false) String imagesToDelete,
                             RedirectAttributes redirectAttributes) throws IOException {

        if (selectDetailType != editDetail.getDetailType().ordinal())
            editDetail.setDetailType(DetailType.values()[selectDetailType]);

        if (!detailService.isDataCorrectly(editDetail)) {
            redirectAttributes.addFlashAttribute("message", "Были введены некорректные данные");
            return "redirect:/admin/editDetail/" + editDetail.getId();
        }

        if (!Objects.equals(uploadImages[0].getOriginalFilename(), ""))
            editDetail.addImages(imageUtil.loadDetailImages(uploadImages, detailFileDirectory, String.valueOf(editDetail.getId())));

        List<String> imagesToDeleteArr = new LinkedList<>();
        if (!imagesToDelete.equals("")) imagesToDeleteArr = List.of(imagesToDelete.split(";"));

        if (imagesToDeleteArr.size() != 0 && !editDetail.isDefaultImage()) {
            imagesToDeleteArr.forEach(editDetail::deleteImage);

            try {
                for (String image : imagesToDeleteArr) {
                    imageUtil.deleteFile(image);
                }
                if (editDetail.isDefaultImage())
                    imageUtil.deleteFile(detailFileDirectory + "detail_" + editDetail.getId());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Ошибка удаления изображения");
            }
        }

        if (!editDetail.getName().equals(oldDetailName)) {
            if (!detailService.saveDetail(editDetail)) {
                model.addAttribute("message", "Деталь с таким названием уже существует");
                model.addAttribute("detail", editDetail);
                model.addAttribute("detailTypes", DetailType.values());
                model.addAttribute("user", user);
                return "editDetail";
            }
        } else detailService.updateDetail(editDetail);

        redirectAttributes.addFlashAttribute("message", "Деталь успешно отредактирована");
        return "redirect:/admin/getDetails";
    }

    @GetMapping("deleteDetail/{detail}")
    public String deleteDetail(RedirectAttributes redirectAttributes, @PathVariable Detail detail) {

        detailService.deleteDetail(detail);

        try {
            imageUtil.deleteFile(detailFileDirectory + "detail_" + detail.getId());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Ошибка удаления");
        }

        redirectAttributes.addFlashAttribute("message", "Деталь успешно удалена");

        return "redirect:/admin/getDetails";
    }

    @GetMapping("addDetail")
    public String getAddDetailPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("user", user);
        model.addAttribute("detailTypes", DetailType.values());

        return "addDetail";
    }

    @PostMapping("addDetail")
    public String addDetail(RedirectAttributes redirectAttributes, @ModelAttribute Detail detail,
                            @RequestParam MultipartFile[] uploadImages,
                            @RequestParam(value = "selectDetailType") int selectDetailTypeOrdinal) throws IOException {

        detail.setDetailType(DetailType.values()[selectDetailTypeOrdinal]);
        if (!detailService.isDataCorrectly(detail)) {
            redirectAttributes.addFlashAttribute("message", "Введены некорректные данные");
            redirectAttributes.addFlashAttribute("detail", detail);
            return "redirect:/admin/addDetail";
        }

        if (!detailService.saveDetail(detail)) {
            redirectAttributes.addFlashAttribute("message", "Деталь с таким названием уже существует");
            redirectAttributes.addFlashAttribute("detail", detail);
            return "redirect:/admin/addDetail";
        }
        if (!Objects.equals(uploadImages[0].getOriginalFilename(), "")) {
            detail.setImages(imageUtil.loadDetailImages(uploadImages, detailFileDirectory, String.valueOf(detail.getId())));
            detailService.updateDetail(detail);
        }
        redirectAttributes.addFlashAttribute("message", "Деталь успешно добавлена");
        return "redirect:/admin/getDetails";
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("getServices")
    public String getServicesPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("services", servService.allServices());
        model.addAttribute("user", user);
        return "getServices";
    }

    @GetMapping("editService/{service}")
    public String getEditServicePage(Model model, @PathVariable Service service, @AuthenticationPrincipal User user) {
        model.addAttribute("service", service);
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("user", user);

        return "editService";
    }

    @PostMapping("editService/{service}")
    public String editService(Model model, @AuthenticationPrincipal User user,
                              @ModelAttribute Service editService,
                              @RequestParam String oldServiceName,
                              @RequestParam int selectServiceType,
                              RedirectAttributes redirectAttributes) {

        if (selectServiceType != editService.getServiceType().ordinal())
            editService.setServiceType(ServiceType.values()[selectServiceType]);
        if (!servService.isDataCorrectly(editService)) {
            redirectAttributes.addFlashAttribute("message", "Были введены некорректные данные");
            return "redirect:/admin/editService/" + editService.getId();
        }

        if (!editService.getName().equals(oldServiceName)) {
            if (!servService.saveService(editService)) {
                model.addAttribute("message", "Услуга с таким названием уже существует");
                model.addAttribute("service", editService);
                model.addAttribute("serviceTypes", ServiceType.values());
                model.addAttribute("user", user);
                return "editService";
            }
        } else servService.updateService(editService);

        redirectAttributes.addFlashAttribute("message", "Услуга успешно отредактирована");
        return "redirect:/admin/getServices";
    }

    @GetMapping("deleteService/{service}")
    public String deleteService(RedirectAttributes redirectAttributes, @PathVariable Service service) {

        servService.deleteService(service);

        redirectAttributes.addFlashAttribute("message", "Услуга успешно удалена");

        return "redirect:/admin/getServices";
    }

    @GetMapping("addService")
    public String getAddServicePage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("user", user);
        model.addAttribute("serviceTypes", ServiceType.values());

        return "addService";
    }

    @PostMapping("addService")
    public String addService(RedirectAttributes redirectAttributes, @ModelAttribute Service service,
                             @RequestParam(value = "selectServiceType") int selectServiceTypeOrdinal) {

        service.setServiceType(ServiceType.values()[selectServiceTypeOrdinal]);
        if (!servService.isDataCorrectly(service)) {
            redirectAttributes.addFlashAttribute("message", "Введены некорректные данные");
            redirectAttributes.addFlashAttribute("service", service);
            return "redirect:/admin/addService";
        }
        if (!servService.saveService(service)) {
            redirectAttributes.addFlashAttribute("message", "Услуга с таким названием уже существует");
            redirectAttributes.addFlashAttribute("service", service);
            return "redirect:/admin/addService";
        }
        redirectAttributes.addFlashAttribute("message", "Услуга успешно добавлена");
        return "redirect:/admin/getServices";
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("getEmployees")
    public String getEmployeesPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("employees", employeeService.allEmployees());
        model.addAttribute("user", user);
        return "getEmployees";
    }

    @GetMapping("editEmployee/{employee}")
    public String getEditEmployeePage(Model model, @PathVariable Employee employee, @AuthenticationPrincipal User user) {
        model.addAttribute("employee", employee);
        model.addAttribute("posts", Post.values());
        model.addAttribute("workSchedules", EmployeeWorkSchedule.values());
        model.addAttribute("user", user);

        return "editEmployee";
    }

    @PostMapping("editEmployee/{employee}")
    public String editEmployee(Model model, @AuthenticationPrincipal User user,
                               @ModelAttribute Employee editEmployee,
                               @RequestParam int selectPost,
                               @RequestParam int selectWorkSchedule,
                               RedirectAttributes redirectAttributes) {

        if (selectPost != editEmployee.getPost().ordinal())
            editEmployee.setPost(Post.values()[selectPost]);
        if (selectWorkSchedule != editEmployee.getEmployeeWorkSchedule().ordinal())
            editEmployee.setEmployeeWorkSchedule(EmployeeWorkSchedule.values()[selectWorkSchedule]);

        if (!employeeService.saveEmployee(editEmployee)) {
            model.addAttribute("message", "Были введены некорректные данные");
            model.addAttribute("employee", editEmployee);
            model.addAttribute("posts", Post.values());
            model.addAttribute("workSchedules", EmployeeWorkSchedule.values());
            model.addAttribute("user", user);
            return "editEmployee";
        }

        redirectAttributes.addFlashAttribute("message", "Данные успешно отредактированы");
        return "redirect:/admin/getEmployees";
    }

    @PostMapping("editEmployeeImage/{employee}")
    public String editEmployeeImage(@PathVariable Employee employee, RedirectAttributes redirectAttributes,
                                    @RequestParam MultipartFile uploadImage) throws IOException {
        saveEmployeeImage(uploadImage, employee);
        redirectAttributes.addFlashAttribute("message", "Фотография успешно изменена");
        return "redirect:/admin/editEmployee/" + employee.getId();
    }

    @GetMapping("deleteEmployee/{employee}")
    public String deleteEmployee(RedirectAttributes redirectAttributes, @PathVariable Employee employee) {

        employeeService.deleteEmployee(employee);

        redirectAttributes.addFlashAttribute("message", "Работник успешно удалён");

        return "redirect:/admin/getEmployees";
    }


    //TODO удаление фото работника
    @GetMapping("addEmployee")
    public String getAddEmployeePage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("user", user);
        model.addAttribute("posts", Post.values());
        model.addAttribute("workSchedules", EmployeeWorkSchedule.values());

        return "addEmployee";
    }

    @PostMapping("addEmployee")
    public String addEmployee(RedirectAttributes redirectAttributes, @ModelAttribute Employee employee,
                              @RequestParam(required = false) MultipartFile uploadImage,
                              @RequestParam int selectPost,
                              @RequestParam int selectWorkSchedule) throws IOException {

        employee.setPost(Post.values()[selectPost]);
        employee.setEmployeeWorkSchedule(EmployeeWorkSchedule.values()[selectWorkSchedule]);

        if (!employeeService.saveEmployee(employee)) {
            redirectAttributes.addFlashAttribute("message", "Введены некорректные данные");
            redirectAttributes.addFlashAttribute("employee", employee);
            return "redirect:/admin/addEmployee";
        }
        saveEmployeeImage(uploadImage, employee);
        redirectAttributes.addFlashAttribute("message", "Работник успешно добавлен");
        return "redirect:/admin/getEmployees";
    }

    private void saveEmployeeImage(MultipartFile uploadImage, Employee employee) throws IOException {
        if (uploadImage == null || Objects.equals(uploadImage.getOriginalFilename(), "")) {
            employee.setImage(defaultEmployeeImagePath);
        } else {
            String fileName = "employee_" + employee.getId() + "_profileImage.png";
            employee.setImage(imageUtil.loadImage(uploadImage, employeeFileDirectory, fileName));
            employeeService.saveEmployee(employee);
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @GetMapping("getServiceOrganizations")
    public String getServiceOrganizationsPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("serviceOrganizations", servOrganizationService.allServiceOrganizations());
        model.addAttribute("user", user);
        return "getServiceOrganizations";
    }

    @GetMapping("editServiceOrganization/{serviceOrganization}")
    public String getEditServiceOrganizationPage(Model model, @PathVariable ServiceOrganization serviceOrganization, @AuthenticationPrincipal User user) {

        model.addAttribute("servOrgWorkSchedules", ServOrgWorkSchedule.values());
        model.addAttribute("serviceOrganization", serviceOrganization);
        model.addAttribute("user", user);

        return "editServiceOrganization";
    }

    @PostMapping("editServiceOrganization/{serviceOrganization}")
    public String editServiceOrganization(@ModelAttribute ServiceOrganization serviceOrganization,
                                          RedirectAttributes redirectAttributes,
                                          @RequestParam String selectWorkSchedules) {

        serviceOrganization.setServOrgWorkSchedule(ServOrgWorkSchedule.values()[Integer.parseInt(selectWorkSchedules)]);

        servOrganizationService.updateServiceOrganization(serviceOrganization);
        redirectAttributes.addFlashAttribute("message", "Данные успешно отредактированы");
        return "redirect:/admin/getServiceOrganizations";
    }

    @GetMapping("deleteServiceOrganization/{serviceOrganization}")
    public String deleteServiceOrganization(RedirectAttributes redirectAttributes,
                                            @PathVariable ServiceOrganization serviceOrganization) {

        servOrganizationService.deleteServiceOrganization(serviceOrganization);

        redirectAttributes.addFlashAttribute("message", "Автосервис успешно удалён");

        return "redirect:/admin/getServiceOrganizations";
    }

    @GetMapping("addServiceOrganization")
    public String getAddServiceOrganizationPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("servOrgWorkSchedules", ServOrgWorkSchedule.values());
        model.addAttribute("user", user);

        return "addServiceOrganization";
    }

    @PostMapping("addServiceOrganization")
    public String addServiceOrganization(Model model, @AuthenticationPrincipal User user,
                                         RedirectAttributes redirectAttributes,
                                         @ModelAttribute ServiceOrganization serviceOrganization,
                                         @RequestParam String selectWorkSchedules) {

        serviceOrganization.setServOrgWorkSchedule(ServOrgWorkSchedule.values()[Integer.parseInt(selectWorkSchedules)]);

        if (!servOrganizationService.saveServiceOrganization(serviceOrganization)) {
            model.addAttribute("serviceOrganization", serviceOrganization);
            model.addAttribute("user", user);
            model.addAttribute("message", "Автосервис по данному адресу уже существует");

            return "addServiceOrganization";
        }
        redirectAttributes.addFlashAttribute("message", "Автосервис успешно добавлен");
        return "redirect:/admin/getServiceOrganizations";
    }

}
