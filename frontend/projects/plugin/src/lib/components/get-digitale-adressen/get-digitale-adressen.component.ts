import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslatePipeModule} from "@valtimo/plugin";
import {BehaviorSubject, combineLatest, Observable, Subscription, take} from "rxjs";
import {GetDigitaleAdressenConfig} from "../../models/get-digitale-adressen-config";
import {AsyncPipe, NgIf} from "@angular/common";
import {CarbonMultiInputModule, FormModule, FormOutput, InputModule} from "@valtimo/components";

@Component({
  selector: 'get-digitale-adressen',
  imports: [
    AsyncPipe,
    FormModule,
    InputModule,
    NgIf,
    PluginTranslatePipeModule,
    CarbonMultiInputModule
  ],
  standalone: true,
  templateUrl: './get-digitale-adressen.component.html'
})
export class GetDigitaleAdressenComponent
  implements FunctionConfigurationComponent, OnInit, OnDestroy {
  @Input() save$: Observable<void>;
  @Input() disabled$: Observable<boolean>;
  @Input() pluginId: string;
  @Input() prefillConfiguration$?: Observable<GetDigitaleAdressenConfig>;
  @Output() valid: EventEmitter<boolean>;
  @Output() configuration: EventEmitter<FunctionConfigurationData> =
    new EventEmitter<FunctionConfigurationData>();

  private saveSubscription!: Subscription;

  private readonly config$ =
    new BehaviorSubject<GetDigitaleAdressenConfig | null>(null);
  private readonly valid$ = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.openSaveSubscription()
  }

  ngOnDestroy(): void {
    this.saveSubscription?.unsubscribe();
  }

  formValueChange(formOutput: FormOutput): void {
    this.config$.next(formOutput as GetDigitaleAdressenConfig);
    this.handleValid(formOutput as GetDigitaleAdressenConfig);
  }

  private handleValid(formOutput: GetDigitaleAdressenConfig): void {
    const valid = !!formOutput.resultPvName &&
      !!formOutput.queryParams;

    this.valid$.next(valid);
    this.valid.emit(valid);
  }

  private openSaveSubscription(): void {
    this.saveSubscription = this.save$?.subscribe((save) => {
      combineLatest([this.config$, this.valid$])
        .pipe(take(1))
        .subscribe(([config, valid]) => {
          if (valid && config) {
            this.configuration.emit(config);
          }
        });
    });
  }
}
