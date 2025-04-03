package com.horizon

import com.horizon.domain.EventDispatcher
import com.horizon.domain.EventManager
import com.horizon.domain.EventProcessor
import com.horizon.entity.Event
import com.horizon.entity.HorizonConfig
import com.horizon.network.FakeNetworkClient
import com.horizon.storage.InMemoryStorage
import com.horizon.utility.FakeNetworkMonitor
import com.horizon.utility.PrintLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HorizonSDKSimulator {

  fun test() {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val config = HorizonConfig(5, 10, 30, 2000, 2000)
    val logger = PrintLogger()

    logger.log("HorizonSDKSimulator", "HorizonSDKSimulator started")

    val storage = InMemoryStorage()
    val networkMonitor = FakeNetworkMonitor(scope, logger)
    val networkClient = FakeNetworkClient(logger, networkMonitor)
    val processor = EventProcessor(config, networkClient, storage, logger)
    val dispatcher = EventDispatcher(config, processor, storage, scope, networkMonitor, logger)
    val manager = EventManager(scope, storage, dispatcher, logger)

    manager.initialise()

    scope.launch {
      for (i in 1..5000) {
        delay((Math.random() * 200).toLong())
        manager.track(
          Event(
            "$i", properties = mapOf(
              "session_id" to "69314b8599c51da9d30808953367275e",
              "created_at" to 1743521901360,
              "store_id" to "",
              "store_name" to "",
              "user_id" to "b8be690c-14a2-4aa6-9bcd-def4755df02a",
              "device_model" to "iPhone 13",
              "device_brand" to "Apple",
              "device_os" to "ios",
              "app_version" to "25.3.3",
              "bundle_version" to "v7",
              "system_version" to "18.3.2",
              "device_id" to "48335452-58AC-42D5-9C76-B7440A976710",
              "source" to "APP_STORE",
              "bundle_v2" to true,
              "bottom_nav_category_exp" to true,
              "preview_mode" to true,
              "enable_cart_ux" to true,
              "paan_corner_cta" to "copy",
              "paan_corner_redirection_url" to "https://www.zeptonow.com/cn/paan-corner/cigarettes/cid/cd50825e-baf8-47fe-9abc-ed9556122a9a/scid/5bcbee47-7c83-4279-80f0-7ecc068496df",
              "paginated_search" to true,
              "plp_recommendations" to false,
              "render_product_card_V2" to true,
              "revamp_sampling" to false,
              "search_preview_mode" to true,
              "search_recommendation_carousal" to false,
              "search_animation_v1" to false,
              "enable_search_filters" to false,
              "nzs_v3_enable" to true,
              "enable_add_more_cart" to true,
              "cart_ptp_carousel_experiment" to true,
              "is_native_event_logger_enabled" to false,
              "is_graphQL_enabled" to true,
              "enable_collapsible_header" to true,
              "truecaller_dropcall" to false,
              "ptp_merge_apis" to true,
              "recently_searched_product_v2" to true,
              "cg_sdk_enabled" to false,
              "device_intelligence" to "{\n" +
                "            \"is_emulated\": false,\n" +
                "            \"is_jailbroken\": false,\n" +
                "            \"running_clone_apps\": false,\n" +
                "            \"running_vpn_spoofers\": false,\n" +
                "            \"virtual_os\": false,\n" +
                "            \"installer_package_name\": \"\",\n" +
                "            \"running_gps_spoofers\": false\n" +
                "          }",
              "first_ever_launch" to false,
              "is_gps_on" to false,
              "activeSplashScreenCampaign" to "thumsup_24_7_1th",
              "signup_variant" to "SIGNUP_V1",
              "app_install_time" to 1743275865457,
              "app_icon" to "Normal",
              "remote_config" to "{\n" +
                "                    \"bundle_v2\": true,\n" +
                "                    \"bottom_nav_category_exp\": true,\n" +
                "                    \"preview_mode\": true,\n" +
                "                    \"enable_cart_ux\": true,\n" +
                "                    \"paan_corner_cta\": \"copy\",\n" +
                "                    \"paan_corner_redirection_url\": \"https://www.zeptonow.com/cn/paan-corner/cigarettes/cid/cd50825e-baf8-47fe-9abc-ed9556122a9a/scid/5bcbee47-7c83-4279-80f0-7ecc068496df\",\n" +
                "                    \"paginated_search\": true,\n" +
                "                    \"plp_recommendations\": false,\n" +
                "                    \"render_product_card_V2\": true,\n" +
                "                    \"revamp_sampling\": false,\n" +
                "                    \"search_preview_mode\": true,\n" +
                "                    \"search_recommendation_carousal\": false,\n" +
                "                    \"search_animation_v1\": false,\n" +
                "                    \"enable_search_filters\": false,\n" +
                "                    \"nzs_v3_enable\": true,\n" +
                "                    \"enable_add_more_cart\": true,\n" +
                "                    \"cart_ptp_carousel_experiment\": true,\n" +
                "                    \"is_native_event_logger_enabled\": false,\n" +
                "                    \"is_graphQL_enabled\": true,\n" +
                "                    \"enable_collapsible_header\": true,\n" +
                "                    \"truecaller_dropcall\": false,\n" +
                "                    \"ptp_merge_apis\": true,\n" +
                "                    \"recently_searched_product_v2\": true,\n" +
                "                    \"cg_sdk_enabled\": false,\n" +
                "                    \"is_revamped_ui\": false,\n" +
                "                    \"zepto_pass_experiment_name\": false,\n" +
                "                    \"isLmsLayout\": false,\n" +
                "                    \"isLmsHookEnabled\": true,\n" +
                "                    \"isLmsKongApi\": true,\n" +
                "                    \"experiments\": {\n" +
                "                        \"AddresschangeHPcart\": {\n" +
                "                            \"experimentName\": \"AddresschangeHPcart\",\n" +
                "                            \"experimentId\": \"dbaa833a-b8ae-4915-8dbc-95f4736df93a\",\n" +
                "                            \"variantName\": \"control\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"BENCHMARK1\": {\n" +
                "                            \"experimentName\": \"BENCHMARK1\",\n" +
                "                            \"experimentId\": \"29b49b0c-4de8-4f45-a7ce-c3105621566b\",\n" +
                "                            \"variantName\": \"CONTROL_BENCHMARK1\",\n" +
                "                            \"data\": {}\n" +
                "                        },\n" +
                "                        \"BENCHMARK2\": {\n" +
                "                            \"experimentName\": \"BENCHMARK2\",\n" +
                "                            \"experimentId\": \"935611ac-245b-4478-a875-03650d5d6bf8\",\n" +
                "                            \"variantName\": \"TEST_BENCHMARK2\",\n" +
                "                            \"data\": {}\n" +
                "                        },\n" +
                "                        \"BENCHMARK7\": {\n" +
                "                            \"experimentName\": \"BENCHMARK7\",\n" +
                "                            \"experimentId\": \"42c0cf86-30f9-4e4d-a112-8eba1dc715cc\",\n" +
                "                            \"variantName\": \"TEST_BENCHMARK7_1\",\n" +
                "                            \"data\": {}\n" +
                "                        },\n" +
                "                        \"Cart_Page_Price_Refresh\": {\n" +
                "                            \"experimentName\": \"Cart_Page_Price_Refresh\",\n" +
                "                            \"experimentId\": \"af91cb72-7f5b-40aa-80bc-df7aeb80d7e9\",\n" +
                "                            \"variantName\": \"control\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"FnV_Preferences_Poll\": {\n" +
                "                            \"experimentName\": \"FnV_Preferences_Poll\",\n" +
                "                            \"experimentId\": \"93c0f71a-419a-457c-93fe-a9b1113fb472\",\n" +
                "                            \"variantName\": \"Test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enabled\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"GoogleMaps_OTP\": {\n" +
                "                            \"experimentName\": \"GoogleMaps_OTP\",\n" +
                "                            \"experimentId\": \"41d8951c-a173-4f2a-b61e-dc93b874914b\",\n" +
                "                            \"variantName\": \"test_gmaps\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"JOURNEY_BENCHMARK\": {\n" +
                "                            \"experimentName\": \"JOURNEY_BENCHMARK\",\n" +
                "                            \"experimentId\": \"a8b87027-ffda-4455-98e5-849ec2bc66c3\",\n" +
                "                            \"variantName\": \"CONTROL_BENCHMARK\",\n" +
                "                            \"data\": {}\n" +
                "                        },\n" +
                "                        \"Maps_OTP\": {\n" +
                "                            \"experimentName\": \"Maps_OTP\",\n" +
                "                            \"experimentId\": \"590b3212-74ac-4cc0-9d01-eed1ab101f03\",\n" +
                "                            \"variantName\": \"Test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"Mobile_Replay\": {\n" +
                "                            \"experimentName\": \"Mobile_Replay\",\n" +
                "                            \"experimentId\": \"691529bf-0bea-448c-946e-94fb3d422721\",\n" +
                "                            \"variantName\": \"Control\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"New_GoogleMaps_OTP\": {\n" +
                "                            \"experimentName\": \"New_GoogleMaps_OTP\",\n" +
                "                            \"experimentId\": \"db577de6-82a0-4ef0-be72-492c6912b3b4\",\n" +
                "                            \"variantName\": \"control_gmaps\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"Pre_Search_Monet\": {\n" +
                "                            \"experimentName\": \"Pre_Search_Monet\",\n" +
                "                            \"experimentId\": \"e885e155-aba3-4887-b194-35786719fd1a\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"bacchat_cart_nudges\": {\n" +
                "                            \"experimentName\": \"bacchat_cart_nudges\",\n" +
                "                            \"experimentId\": \"a1d1ded2-2451-40c1-a767-154c30ddfde4\",\n" +
                "                            \"variantName\": \"Test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"bachat_search_toggle_v2\": {\n" +
                "                            \"experimentName\": \"bachat_search_toggle_v2\",\n" +
                "                            \"experimentId\": \"2903e3ff-ff5d-4271-840d-10f46114b33e\",\n" +
                "                            \"variantName\": \"switch_with_coachmark\",\n" +
                "                            \"data\": {\n" +
                "                                \"enableCoachmark\": true,\n" +
                "                                \"enableSwitch\": false,\n" +
                "                                \"enableTopToggle\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"cafe_ymal_postatc_new\": {\n" +
                "                            \"experimentName\": \"cafe_ymal_postatc_new\",\n" +
                "                            \"experimentId\": \"84f26b4e-21dc-44a3-9062-178294f0ca6b\",\n" +
                "                            \"variantName\": \"Control\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"home_optimization_m4\": {\n" +
                "                            \"experimentName\": \"home_optimization_m4\",\n" +
                "                            \"experimentId\": \"0f26a2a9-0ce0-4400-a23d-64610801ff08\",\n" +
                "                            \"variantName\": \"Test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enableRenderQueueOnPC\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"horizontal_carousel_optimisation\": {\n" +
                "                            \"experimentName\": \"horizontal_carousel_optimisation\",\n" +
                "                            \"experimentId\": \"610062d1-c0ed-469d-965d-7a5138e7fd26\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"lms_browse\": {\n" +
                "                            \"experimentName\": \"lms_browse\",\n" +
                "                            \"experimentId\": \"00598f81-e0da-4d81-911b-91e858fbb885\",\n" +
                "                            \"variantName\": \"control\",\n" +
                "                            \"data\": {\n" +
                "                                \"enabledForDeeplink\": false,\n" +
                "                                \"enabledForPlp\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"lms_home_page_enabled\": {\n" +
                "                            \"experimentName\": \"lms_home_page_enabled\",\n" +
                "                            \"experimentId\": \"f6fce71b-10fb-4193-ac86-3ff41bfba4ba\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"lms_home_page_internal_enabled\": {\n" +
                "                            \"experimentName\": \"lms_home_page_internal_enabled\",\n" +
                "                            \"experimentId\": \"e261133e-dcb7-48fd-b1ac-4547d6701925\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"open_srp_cross_sell_for_itemized_products\": {\n" +
                "                            \"experimentName\": \"open_srp_cross_sell_for_itemized_products\",\n" +
                "                            \"experimentId\": \"1579549b-d010-4bda-b6ea-046bbc19646a\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"post_cart_bottomsheet_SS\": {\n" +
                "                            \"experimentName\": \"post_cart_bottomsheet_SS\",\n" +
                "                            \"experimentId\": \"d9d86a23-be08-4dac-bfb3-08c997b6d74c\",\n" +
                "                            \"variantName\": \"test_without_PTP\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true,\n" +
                "                                \"enablePCR\": true,\n" +
                "                                \"enablePTP\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"refund_revamp\": {\n" +
                "                            \"experimentName\": \"refund_revamp\",\n" +
                "                            \"experimentId\": \"a24787ea-f5d4-4102-be6b-996710e6069f\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"scope_search_v1\": {\n" +
                "                            \"experimentName\": \"scope_search_v1\",\n" +
                "                            \"experimentId\": \"6cf596fa-603c-4e8e-b361-fbfdb0941068\",\n" +
                "                            \"variantName\": \"test_hide_bottom_nav\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true,\n" +
                "                                \"type\": \"hide_bottom_nav\"\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"search_query_limit\": {\n" +
                "                            \"experimentName\": \"search_query_limit\",\n" +
                "                            \"experimentId\": \"0fc7d810-45c8-4ac2-a688-a19e3ae87753\",\n" +
                "                            \"variantName\": \"test\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": true,\n" +
                "                                \"limit\": 2\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"test_address_revamp\": {\n" +
                "                            \"experimentName\": \"test_address_revamp\",\n" +
                "                            \"experimentId\": \"b0a5f230-97bc-4735-b07d-8c81d436cb05\",\n" +
                "                            \"variantName\": \"control_add\",\n" +
                "                            \"data\": {\n" +
                "                                \"enable\": false\n" +
                "                            }\n" +
                "                        },\n" +
                "                        \"theme_based_buying_v2\": {\n" +
                "                            \"experimentName\": \"theme_based_buying_v2\",\n" +
                "                            \"experimentId\": \"f47fd7b4-56be-4ef9-a5df-e2137e766ca9\",\n" +
                "                            \"variantName\": \"test2\",\n" +
                "                            \"data\": {}\n" +
                "                        }\n" +
                "                    }\n" +
                "                }"
            )
          )
        )
      }

      manager.shutdown()

      while (true) {
        delay(1000)
        networkClient.printItems()
      }
    }
  }
}
