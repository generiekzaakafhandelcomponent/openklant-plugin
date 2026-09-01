import {ComponentFixture, TestBed} from '@angular/core/testing';
import {of, Subject} from 'rxjs';

import {UpdateBijlageComponent} from './update-bijlage.component';
import {UpdateBijlageConfig} from '../../models/update-bijlage-config';

describe('UpdateBijlageComponent', () => {
    let component: UpdateBijlageComponent;
    let fixture: ComponentFixture<UpdateBijlageComponent>;
    let save$: Subject<void>;

    const emptyConfig = {
        uuid: '',
        resultPvName: '',
    } satisfies Partial<UpdateBijlageConfig>;
    const validConfig = {...emptyConfig, uuid: 'x', resultPvName: 'x'} satisfies Partial<UpdateBijlageConfig>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [UpdateBijlageComponent]
        })
            .overrideTemplate(UpdateBijlageComponent, '')
            .compileComponents();

        save$ = new Subject<void>();

        fixture = TestBed.createComponent(UpdateBijlageComponent);
        component = fixture.componentInstance;

        component.save$ = save$;
        component.disabled$ = of(false);
        component.pluginId = 'openklant';
        component.prefillConfiguration$ = of(emptyConfig as UpdateBijlageConfig);

        fixture.detectChanges();
    });

    afterEach(() => save$.complete());

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should report an incomplete configuration as invalid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(emptyConfig);

        expect(emitted).toEqual([false]);
    });

    it('should report a configuration with every required field as valid', () => {
        const emitted: boolean[] = [];
        component.valid.subscribe(valid => emitted.push(valid));

        component.formValueChange(validConfig);

        expect(emitted).toEqual([true]);
    });

    it('should emit the configuration on save when it is valid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(validConfig);
        save$.next();

        expect(configurations).toEqual([validConfig]);
    });

    it('should not emit the configuration on save when it is invalid', () => {
        const configurations: unknown[] = [];
        component.configuration.subscribe(configuration => configurations.push(configuration));

        component.formValueChange(emptyConfig);
        save$.next();

        expect(configurations).toEqual([]);
    });
});
