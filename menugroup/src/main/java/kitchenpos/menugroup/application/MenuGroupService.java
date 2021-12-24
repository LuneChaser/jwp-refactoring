package kitchenpos.menugroup.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.menugroup.domain.MenuGroupRepository;
import kitchenpos.menugroup.dto.MenuGroupDto;
import kitchenpos.menugroup.exception.NotFoundMenuGroupException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuGroupService {
    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupService(final MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    @Transactional
    public MenuGroupDto create(final MenuGroupDto menuGroup) {
        return MenuGroupDto.of(menuGroupRepository.save(menuGroup.toMenuGroup()));
    }

    public List<MenuGroupDto> list() {
        return menuGroupRepository.findAll().stream()
                                    .map(MenuGroupDto::of)
                                    .collect(Collectors.toList());
    }

    public MenuGroup findById(Long menuGroupId) {
        return menuGroupRepository.findById(menuGroupId).orElseThrow(NotFoundMenuGroupException::new);
    }
}