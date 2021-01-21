package kitchenpos.acceptance.menu;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;

public class MenuGroupAcceptanceTest extends MenuGroupAcceptance {

	@DisplayName("메뉴 그룹을 생성한다.")
	@Test
	void createMenuGroupTest() {
		// given
		MenuGroup menuGroup = MenuGroup.of(null, "두마리메뉴");

		// when
		ExtractableResponse<Response> response = 메뉴_그룹_등록_요청(menuGroup);

		// then
		메뉴_그룹_등록됨(response);
	}

	@DisplayName("동일한 메뉴 그룹을 생성한다.")
	@Test
	void createDuplicateMenuGroupTest() {
		// given
		MenuGroup menuGroup = MenuGroup.of(null, "두마리메뉴");

		// when
		ExtractableResponse<Response> response1 = 메뉴_그룹_등록_요청(menuGroup);
		ExtractableResponse<Response> response2 = 메뉴_그룹_등록_요청(menuGroup);

		// then
		메뉴_그룹_등록됨(response1);
		메뉴_그룹_등록됨(response2);
	}

	@DisplayName("비어있는 메뉴 그룹을 생성한다.")
	@Test
	void createBlankMenuGroupTest() {
		// given
		MenuGroup menuGroup = MenuGroup.of(null, "");

		// when
		ExtractableResponse<Response> response = 메뉴_그룹_등록_요청(menuGroup);

		// then
		메뉴_그룹_등록됨(response);
	}

	@DisplayName("메뉴 그룹을 조회한다.")
	@Test
	void getMenuGroupsTest() {
		// given
		ExtractableResponse<Response> createResponse1 = 메뉴_그룹_등록되어_있음("한마리메뉴");
		ExtractableResponse<Response> createResponse2 = 메뉴_그룹_등록되어_있음("두마리메뉴");

		// when
		ExtractableResponse<Response> response = 메뉴_그룹_조회_요청();

		// then
		메뉴_그룹_목록_조회됨(response);
		메뉴_그룹_목록_포함됨(response, Arrays.asList(createResponse1, createResponse2));
	}
}

