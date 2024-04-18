package SSU.MyMedicine.service;

import SSU.MyMedicine.DAO.PrescriptionRepository;
import SSU.MyMedicine.VO.PrescriptionVO;
import SSU.MyMedicine.entity.Medicine;
import SSU.MyMedicine.entity.Prescription;
import SSU.MyMedicine.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PrescriptionService {
    final private PrescriptionRepository prescriptionRepository;
    final private MedicineService medicineService;
    final private UserService userService;

    @Autowired
    PrescriptionService(PrescriptionRepository prescriptionRepository, MedicineService medicineService, UserService userService) {
        this.prescriptionRepository = prescriptionRepository;
        this.medicineService = medicineService;
        this.userService = userService;
    }

    @Value("${upload.path}")
    private String uploadPath;

    @Transactional
    public Prescription save(MultipartFile file, PrescriptionVO prescription) throws IOException {
        // save image
        Random random = new Random();
        int randomNumber = random.nextInt(10000000);
        String imageName = prescription.getUid().toString() + "0000" + Integer.toString(randomNumber) + ".jpg";
        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + imageName);
        Files.write(path, bytes);

        //         save new medicine to DB
        medicineService.saveIfNotThere(prescription.getMedList());

        List<Medicine> medicineList = new ArrayList<>();
        for (String medName : prescription.getMedList()) {
            Medicine medicine = medicineService.findByMedName(medName);
            medicineList.add(medicine);
        }

        User prescUser = userService.findByUid(prescription.getUid());

        Prescription newPresc = Prescription.builder()
                .regDate(prescription.getRegDate())
                .duration(prescription.getDuration())
                .medList(medicineList)
                .imageNum(imageName)
                .user(prescUser)
                .build();

        return prescriptionRepository.save(newPresc);
    }
    public void runImageWarpingPy(String imageFileName) throws IOException {
        int lastDotIndex = imageFileName.lastIndexOf('.');
        String imageNum = imageFileName.substring(0, lastDotIndex);
        ProcessBuilder processBuilder = new ProcessBuilder("python", "/home/ubuntu/warp.py", imageNum);
        processBuilder.start();
    }
    public Prescription findByPid(Integer pid){
        Prescription prescription = prescriptionRepository.findByPid(pid);
        if (prescription == null){
            throw new EntityNotFoundException("Entity not found with pid: " + pid);
        }
        return prescription;
    }
    public byte[] getPrescImg(String imgPath) throws IOException{
        InputStream imageStream = new FileInputStream(uploadPath + imgPath);
        byte[] imageBytes = null;
        imageBytes = FileCopyUtils.copyToByteArray(imageStream);
        imageStream.close();
        return imageBytes;
    }
    public void delete(Prescription prescription){
        prescriptionRepository.delete(prescription);
    }
}
