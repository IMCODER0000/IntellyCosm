package gachonproject.web.service;


import gachonproject.web.domain.Admin;
import gachonproject.web.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public void updateAdmin(Admin admin) {
        adminRepository.update(admin);
    }


    public void signUp(Admin admin) {
        adminRepository.signUp(admin);
    }




    public Admin findById(Long admin_id){
        return adminRepository.findById(admin_id);
    }



}
