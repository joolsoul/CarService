package ru.kudinov.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kudinov.model.Detail;
import ru.kudinov.model.Service;
import ru.kudinov.model.User;
import ru.kudinov.model.enums.DetailType;
import ru.kudinov.model.enums.RequestStatus;
import ru.kudinov.model.enums.ServiceType;
import ru.kudinov.service.*;
import ru.kudinov.util.ImageUtil;

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
    private final PromoService promoService;
    private final ServService servService;
    private final ServOrganizationService servOrganizationService;
    private final EmployeeService employeeService;
    private final ImageUtil imageUtil;

    @Value("${detail.file-directory}")
    private String detailFileDirectory;

    public AdminController(UserService userService, DetailService detailService, RequestService requestService,
                           PromoService promoService, ServService servService,
                           ServOrganizationService servOrganizationService, EmployeeService employeeService,
                           ImageUtil imageUtil) {
        this.userService = userService;
        this.detailService = detailService;
        this.requestService = requestService;
        this.promoService = promoService;
        this.servService = servService;
        this.servOrganizationService = servOrganizationService;
        this.employeeService = employeeService;
        this.imageUtil = imageUtil;
    }


    @GetMapping("getUsers")
    public String getUserListPage(Model model, @AuthenticationPrincipal User user) {

        model.addAttribute("users", userService.allUsers());
        model.addAttribute("user", user);

        return "userList";
    }

    @GetMapping("deleteUser/{user}")
    public String deleteUser(RedirectAttributes redirectAttributes, @PathVariable User user) throws IOException {

        imageUtil.deleteFile(user.getImage());
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

        if (imagesToDeleteArr.size() != 0) {
            imagesToDeleteArr.forEach(editDetail::deleteImage);

            try {
                for (String image : imagesToDeleteArr) {
                    imageUtil.deleteFile(image);
                }
                if (editDetail.getImages().size() == 0)
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
            for (String image : detail.getImages()) {
                imageUtil.deleteFile(image);
            }
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


//    @GetMapping("getPromotions")
//    public String getPromotionsPage(Model model) {
//
//        return "/";
//    }
//
//    @PostMapping("addPromo")
//    public String addPromo(Model model) {
//
//        return "/";
//    }
//    @GetMapping("getServiceOrganizations")
//    public String getServiceOrganizationsPage(Model model) {
//
//        return "/";
//    }
//
//    @PostMapping("addServiceOrganization")
//    public String addServiceOrganization(Model model) {
//
//        return "/";
//    }
//
//    @GetMapping("getEmployees")
//    public String getEmployeesPage(Model model) {
//
//        return "/";
//    }
//
//    @PostMapping("addEmployee")
//    public String addEmployee(Model model) {
//
//        return "/";
//    }
//
//
//    //TODO: Сделать отображения информации о конкретном пользователе, основываясь на pathVariable
//    //TODO: редактирование машин, информации о пользователе и его прав
//    @GetMapping("/user/{user}")
//    public String getUserPage(Model model, @PathVariable String user) {
//        return "index";
//    }

}
