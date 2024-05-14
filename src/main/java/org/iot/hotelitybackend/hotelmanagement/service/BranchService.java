package org.iot.hotelitybackend.hotelmanagement.service;

import java.util.List;
import java.util.Map;

import org.iot.hotelitybackend.hotelmanagement.dto.BranchDTO;
import org.iot.hotelitybackend.hotelmanagement.vo.RequestModifyBranch;
import org.iot.hotelitybackend.hotelmanagement.vo.RequestRegistBranch;

public interface BranchService {

	Map<String, Object> selectAllBranches(int pageNum);

	Map<String, Object> registBranch(RequestRegistBranch requestRegistBranch);

	Map<String, Object> modifyBranchInfo(RequestModifyBranch requestModifyBranch, String branchCodePk);

	Map<String, Object> deleteBranch(String branchCodePk);
}
