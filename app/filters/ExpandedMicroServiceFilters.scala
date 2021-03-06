/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package filters

import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
import uk.gov.hmrc.play.bootstrap.backend.filters.BackendFilters

class ExpandedMicroServiceFilters @Inject()(
                        microserviceFilters: BackendFilters,
                        corsFilter: CORSFilter)
  extends DefaultHttpFilters(microserviceFilters.filters :+ corsFilter: _*)
