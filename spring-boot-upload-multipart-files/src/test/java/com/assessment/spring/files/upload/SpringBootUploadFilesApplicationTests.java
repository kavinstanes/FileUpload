package com.assessment.spring.files.upload;

import java.io.InputStream;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import com.assessment.spring.files.upload.controller.FilesController;
import com.assessment.spring.files.upload.service.FilesStorageService;

@RunWith(SpringRunner.class)
@SpringBootTest()
class SpringBootUploadFilesApplicationTests {
	private InputStream is;
	private MockMvc mockMvc;

	@Spy
	@InjectMocks
	private FilesController controller = new FilesController();

	@Mock
	private FilesStorageService storageService;

	@Test
	void contextLoads() throws Exception {
		try {
			mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
			System.out.println(controller.getClass().getClassLoader().getResource("dummyFile.txt").getPath());
			is = controller.getClass().getClassLoader().getResourceAsStream("dummyFile.txt");

			MockMultipartFile file = new MockMultipartFile("file", "dummyFile.txt", "multipart/form-data", is);

			MvcResult result = mockMvc
					.perform(MockMvcRequestBuilders.multipart("/upload").file(file)
							.contentType(MediaType.MULTIPART_FORM_DATA))
					.andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
			Assert.assertEquals(200, result.getResponse().getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
