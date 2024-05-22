package my.school

@SpringBootTest
class MyServiceTest {

    @Autowired
    private MyService myService;

    @MockBean
    private RestClient restClient;

    @Test
    void testFetchData() {
        // Given
        String expectedResponse = "some data";

        RestClient.RequestSpec requestSpec = Mockito.mock(RestClient.RequestSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        Mockito.when(restClient.get()).thenReturn(requestSpec);
        Mockito.when(requestSpec.uri("http://example.com/api/data")).thenReturn(requestSpec);
        Mockito.when(requestSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(String.class)).thenReturn(expectedResponse);

        // When
        String actualResponse = myService.fetchData();

        // Then
        assertEquals(expectedResponse, actualResponse);
    }
}
