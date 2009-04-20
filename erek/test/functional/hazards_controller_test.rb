require 'test_helper'

class HazardsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:hazards)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create hazard" do
    assert_difference('Hazard.count') do
      post :create, :hazard => { }
    end

    assert_redirected_to hazard_path(assigns(:hazard))
  end

  test "should show hazard" do
    get :show, :id => hazards(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => hazards(:one).to_param
    assert_response :success
  end

  test "should update hazard" do
    put :update, :id => hazards(:one).to_param, :hazard => { }
    assert_redirected_to hazard_path(assigns(:hazard))
  end

  test "should destroy hazard" do
    assert_difference('Hazard.count', -1) do
      delete :destroy, :id => hazards(:one).to_param
    end

    assert_redirected_to hazards_path
  end
end
